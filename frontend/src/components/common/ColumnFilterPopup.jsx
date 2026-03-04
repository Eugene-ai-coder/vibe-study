import { useState, useRef, useMemo, useCallback } from 'react'
import { createPortal } from 'react-dom'
import useClickOutside from '../../hooks/useClickOutside'
import usePopupPosition from '../../hooks/usePopupPosition'

/* ── 필터 조건 목록 ─────────────────────────────── */
const CONDITIONS = [
  { value: 'contains',   label: '포함' },
  { value: 'equals',     label: '일치' },
  { value: 'startsWith', label: '시작 문자' },
  { value: 'endsWith',   label: '끝 문자' },
  { value: 'isEmpty',    label: '비어 있음' },
  { value: 'isNotEmpty', label: '비어 있지 않음' },
]

/* ── 커스텀 필터 함수 (TanStack filterFn으로 등록) ── */
export function advancedFilterFn(row, columnId, filterValue) {
  if (!filterValue) return true

  const { condition, text, checkedValues } = filterValue
  const cellRaw = row.getValue(columnId)
  const cellVal = cellRaw != null ? String(cellRaw) : ''

  /* 조건 필터 */
  let conditionPass = true
  if (condition && condition !== 'contains') {
    switch (condition) {
      case 'equals':     conditionPass = cellVal.toLowerCase() === (text ?? '').toLowerCase(); break
      case 'startsWith': conditionPass = cellVal.toLowerCase().startsWith((text ?? '').toLowerCase()); break
      case 'endsWith':   conditionPass = cellVal.toLowerCase().endsWith((text ?? '').toLowerCase()); break
      case 'isEmpty':    conditionPass = cellVal.trim() === ''; break
      case 'isNotEmpty': conditionPass = cellVal.trim() !== ''; break
      default:           conditionPass = true
    }
  } else if (text) {
    conditionPass = cellVal.toLowerCase().includes(text.toLowerCase())
  }

  /* 체크박스 필터 */
  let checkPass = true
  if (checkedValues && checkedValues.length > 0) {
    checkPass = checkedValues.includes(cellRaw != null ? String(cellRaw) : '')
  }

  return conditionPass && checkPass
}

/* ══════════════════════════════════════════════════
   ColumnFilterPopup
   ══════════════════════════════════════════════════ */
export default function ColumnFilterPopup({ column, data, onClose, anchorRect }) {
  const popupRef = useRef(null)
  const pos = usePopupPosition(anchorRect, popupRef)

  /* 기존 필터 값에서 초기 상태 복원 */
  const currentFilter = column.getFilterValue()
  const [condition, setCondition] = useState(currentFilter?.condition ?? 'contains')
  const [text, setText] = useState(currentFilter?.text ?? '')
  const [checkedValues, setCheckedValues] = useState(currentFilter?.checkedValues ?? [])

  /* 고유값 추출 */
  const uniqueValues = useMemo(() => {
    const colId = column.id
    const vals = new Set()
    data.forEach(row => {
      const v = row[colId]
      vals.add(v != null ? String(v) : '')
    })
    return [...vals].sort()
  }, [column.id, data])

  /* 전체 선택 상태 */
  const allChecked = checkedValues.length === uniqueValues.length && uniqueValues.length > 0

  /* Click Outside 처리 */
  useClickOutside(popupRef, onClose)

  /* 전체 선택 토글 */
  const toggleAll = useCallback(() => {
    setCheckedValues(allChecked ? [] : [...uniqueValues])
  }, [allChecked, uniqueValues])

  /* 개별 체크박스 토글 */
  const toggleValue = useCallback((val) => {
    setCheckedValues(prev =>
      prev.includes(val) ? prev.filter(v => v !== val) : [...prev, val]
    )
  }, [])

  /* 적용 */
  const handleApply = () => {
    const hasCondition = condition === 'isEmpty' || condition === 'isNotEmpty' || text.trim() !== ''
    const hasChecked = checkedValues.length > 0 && checkedValues.length < uniqueValues.length

    if (!hasCondition && !hasChecked) {
      column.setFilterValue(undefined)
    } else {
      column.setFilterValue({
        condition: hasCondition ? condition : undefined,
        text: hasCondition ? text : undefined,
        checkedValues: hasChecked ? checkedValues : undefined,
      })
    }
    onClose()
  }

  /* 초기화 */
  const handleReset = () => {
    setCondition('contains')
    setText('')
    setCheckedValues([])
    column.setFilterValue(undefined)
    onClose()
  }

  const isNoInputCondition = condition === 'isEmpty' || condition === 'isNotEmpty'

  return createPortal(
    <div
      ref={popupRef}
      style={{ position: 'fixed', top: pos.top, left: pos.left }}
      className="w-60 bg-white rounded-lg shadow-lg border border-gray-200 z-[9999]"
      onClick={e => e.stopPropagation()}
    >
      {/* 조건 선택 + 텍스트 검색 */}
      <div className="p-3 border-b border-gray-100">
        <label className="block text-xs text-gray-500 mb-1">조건</label>
        <select
          value={condition}
          onChange={e => setCondition(e.target.value)}
          className="w-full h-7 px-2 text-xs border border-gray-300 rounded focus:outline-none focus:border-blue-400 bg-white"
        >
          {CONDITIONS.map(c => (
            <option key={c.value} value={c.value}>{c.label}</option>
          ))}
        </select>
        {!isNoInputCondition && (
          <input
            value={text}
            onChange={e => setText(e.target.value)}
            onKeyDown={e => e.key === 'Enter' && handleApply()}
            placeholder="검색어 입력..."
            className="w-full h-7 px-2 mt-2 text-xs border border-gray-300 rounded focus:outline-none focus:border-blue-400"
            autoFocus
          />
        )}
      </div>

      {/* 체크박스 리스트 */}
      <div className="p-3 border-b border-gray-100">
        <label className="block text-xs text-gray-500 mb-2">값 선택</label>
        <label className="flex items-center gap-2 text-xs text-gray-700 mb-1 cursor-pointer hover:text-blue-600">
          <input
            type="checkbox"
            checked={allChecked}
            onChange={toggleAll}
            className="rounded border-gray-300 text-blue-600 focus:ring-blue-500 h-3.5 w-3.5"
          />
          <span className="font-medium">모두 선택</span>
        </label>
        <div className="max-h-32 overflow-y-auto mt-1 space-y-0.5">
          {uniqueValues.map(val => (
            <label key={val} className="flex items-center gap-2 text-xs text-gray-600 cursor-pointer py-0.5 px-1 rounded hover:bg-gray-50">
              <input
                type="checkbox"
                checked={checkedValues.includes(val)}
                onChange={() => toggleValue(val)}
                className="rounded border-gray-300 text-blue-600 focus:ring-blue-500 h-3.5 w-3.5"
              />
              <span className="truncate">{val || '(빈 값)'}</span>
            </label>
          ))}
        </div>
      </div>

      {/* 액션 버튼 */}
      <div className="flex items-center justify-between p-2">
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
