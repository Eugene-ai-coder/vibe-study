import { useState, useRef, useCallback } from 'react'
import { createPortal } from 'react-dom'
import useClickOutside from '../../hooks/useClickOutside'
import usePopupPosition from '../../hooks/usePopupPosition'
import {
  DndContext,
  closestCenter,
  KeyboardSensor,
  PointerSensor,
  useSensor,
  useSensors,
} from '@dnd-kit/core'
import {
  arrayMove,
  SortableContext,
  sortableKeyboardCoordinates,
  verticalListSortingStrategy,
  useSortable,
} from '@dnd-kit/sortable'
import { CSS } from '@dnd-kit/utilities'

/* ── 드래그 핸들 아이콘 ─────────────────────────── */
function GripIcon() {
  return (
    <svg viewBox="0 0 16 16" fill="currentColor" className="w-3.5 h-3.5 text-gray-400">
      <circle cx="5" cy="3" r="1.2" />
      <circle cx="11" cy="3" r="1.2" />
      <circle cx="5" cy="8" r="1.2" />
      <circle cx="11" cy="8" r="1.2" />
      <circle cx="5" cy="13" r="1.2" />
      <circle cx="11" cy="13" r="1.2" />
    </svg>
  )
}

/* ── 개별 정렬 가능한 컬럼 항목 ─────────────────── */
function SortableColumnItem({ id, label, visible, onToggle, disableToggle }) {
  const {
    attributes,
    listeners,
    setNodeRef,
    transform,
    transition,
    isDragging,
  } = useSortable({ id })

  const style = {
    transform: CSS.Transform.toString(transform),
    transition,
  }

  return (
    <div
      ref={setNodeRef}
      style={style}
      className={`flex items-center gap-2 px-2 py-1.5 rounded text-xs select-none ${
        isDragging ? 'bg-blue-50 shadow-md ring-1 ring-blue-300 z-50' : 'hover:bg-gray-50'
      }`}
    >
      {/* 드래그 핸들 */}
      <button
        {...attributes}
        {...listeners}
        className="cursor-grab active:cursor-grabbing p-0.5 rounded hover:bg-gray-200 touch-none"
        tabIndex={-1}
      >
        <GripIcon />
      </button>

      {/* 체크박스 */}
      <label className="flex items-center gap-2 flex-1 min-w-0 cursor-pointer">
        <input
          type="checkbox"
          checked={visible}
          onChange={() => onToggle(id)}
          disabled={disableToggle}
          className="rounded border-gray-300 text-blue-600 focus:ring-blue-500 h-3.5 w-3.5 disabled:opacity-40"
        />
        <span className={`truncate ${!visible ? 'text-gray-400' : 'text-gray-700'}`}>
          {label}
        </span>
      </label>
    </div>
  )
}

/* ══════════════════════════════════════════════════
   ColumnSettingsPopup
   ══════════════════════════════════════════════════ */
export default function ColumnSettingsPopup({
  columns,       // 전체 컬럼 정의 배열 [{ accessorKey, header, ... }]
  columnOrder,   // 현재 컬럼 순서 배열 [string]
  hiddenColumns, // 숨겨진 컬럼 Set
  onApply,       // (newOrder: string[], newHidden: Set<string>) => void
  onReset,       // 초기화 콜백
  onClose,
  anchorRect,
}) {
  const popupRef = useRef(null)
  const pos = usePopupPosition(anchorRect, popupRef, { alignRight: true })

  /* 로컬 편집 상태 */
  const [localOrder, setLocalOrder] = useState([...columnOrder])
  const [localHidden, setLocalHidden] = useState(new Set(hiddenColumns))

  /* 컬럼 ID → header 라벨 맵 */
  const labelMap = {}
  columns.forEach(col => {
    const key = col.accessorKey ?? col.id
    labelMap[key] = typeof col.header === 'string' ? col.header : key
  })

  /* Click Outside */
  useClickOutside(popupRef, onClose)

  /* DnD 센서 */
  const sensors = useSensors(
    useSensor(PointerSensor, { activationConstraint: { distance: 4 } }),
    useSensor(KeyboardSensor, { coordinateGetter: sortableKeyboardCoordinates }),
  )

  const handleDragEnd = useCallback((event) => {
    const { active, over } = event
    if (over && active.id !== over.id) {
      setLocalOrder(prev => {
        const oldIdx = prev.indexOf(active.id)
        const newIdx = prev.indexOf(over.id)
        return arrayMove(prev, oldIdx, newIdx)
      })
    }
  }, [])

  /* 가시성 토글 */
  const toggleVisibility = useCallback((colId) => {
    setLocalHidden(prev => {
      const next = new Set(prev)
      if (next.has(colId)) {
        next.delete(colId)
      } else {
        // 최소 1개 유지
        const visibleCount = localOrder.filter(id => !next.has(id)).length
        if (visibleCount <= 1) return prev
        next.add(colId)
      }
      return next
    })
  }, [localOrder])

  /* 적용 — 실시간 반영 */
  const handleApply = () => {
    onApply(localOrder, localHidden)
    onClose()
  }

  /* 초기화 */
  const handleReset = () => {
    onReset()
    onClose()
  }

  const visibleCount = localOrder.filter(id => !localHidden.has(id)).length

  return createPortal(
    <div
      ref={popupRef}
      style={{ position: 'fixed', top: pos.top, left: pos.left }}
      className="w-60 bg-white rounded-lg shadow-lg border border-gray-200 z-[9999]"
      onClick={e => e.stopPropagation()}
    >
      {/* 헤더 */}
      <div className="px-3 py-2 border-b border-gray-100">
        <div className="flex items-center justify-between">
          <span className="text-xs font-semibold text-gray-700">컬럼 설정</span>
          <span className="text-[10px] text-gray-400">{visibleCount}/{localOrder.length}개 표시</span>
        </div>
      </div>

      {/* 컬럼 리스트 (드래그 가능) */}
      <div className="max-h-64 overflow-y-auto p-2">
        <DndContext
          sensors={sensors}
          collisionDetection={closestCenter}
          onDragEnd={handleDragEnd}
        >
          <SortableContext items={localOrder} strategy={verticalListSortingStrategy}>
            {localOrder.map(colId => (
              <SortableColumnItem
                key={colId}
                id={colId}
                label={labelMap[colId] ?? colId}
                visible={!localHidden.has(colId)}
                onToggle={toggleVisibility}
                disableToggle={!localHidden.has(colId) && visibleCount <= 1}
              />
            ))}
          </SortableContext>
        </DndContext>
      </div>

      {/* 액션 버튼 */}
      <div className="flex items-center justify-between p-2 border-t border-gray-100">
        <button
          onClick={handleReset}
          className="h-7 px-3 text-xs text-gray-500 hover:text-red-500 hover:bg-red-50 rounded"
        >
          초기화
        </button>
        <button
          onClick={handleApply}
          className="h-7 px-4 text-xs bg-blue-600 text-white rounded hover:bg-blue-700"
        >
          적용
        </button>
      </div>
    </div>,
    document.body
  )
}
