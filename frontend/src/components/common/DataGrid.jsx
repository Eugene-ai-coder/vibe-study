import { useState, useMemo, useCallback, useRef, useEffect } from 'react'
import {
  useReactTable,
  getCoreRowModel,
  getSortedRowModel,
  getFilteredRowModel,
  flexRender,
} from '@tanstack/react-table'
import ColumnFilterPopup, { advancedFilterFn } from './ColumnFilterPopup'
import ColumnSettingsPopup from './ColumnSettingsPopup'

/* ── 리사이즈 핸들 ─────────────────────────────── */
function ResizeHandle({ header }) {
  return (
    <div
      onMouseDown={header.getResizeHandler()}
      onTouchStart={header.getResizeHandler()}
      className={`absolute right-0 top-0 h-full w-1.5 cursor-col-resize select-none touch-none hover:bg-blue-400 ${
        header.column.getIsResizing() ? 'bg-blue-500' : ''
      }`}
    />
  )
}

/* ── 정렬 아이콘 ───────────────────────────────── */
function SortIcon({ direction }) {
  if (!direction) return <span className="text-gray-300 ml-1">↕</span>
  return <span className="text-blue-600 ml-1">{direction === 'asc' ? '↑' : '↓'}</span>
}

/* ── 필터 아이콘 (SVG funnel) ──────────────────── */
function FilterIcon({ active }) {
  return (
    <svg
      viewBox="0 0 16 16"
      fill="currentColor"
      className={`w-3 h-3 shrink-0 ${active ? 'text-blue-600' : 'text-gray-400 hover:text-gray-600'}`}
    >
      <path d="M1 2a1 1 0 011-1h12a1 1 0 01.8 1.6L10 8.5V13a1 1 0 01-.45.83l-2 1.34A1 1 0 016 14.34V8.5L1.2 2.6A1 1 0 011 2z" />
    </svg>
  )
}

/* ── 드래그 가능한 헤더 셀 ─────────────────────── */
function DraggableHeader({ header, onDragStart, onDragOver, onDrop, isPinned, data }) {
  const { column } = header
  const canSort = column.getCanSort()
  const canFilter = column.getCanFilter()
  const sortDir = column.getIsSorted()
  const filterValue = column.getFilterValue()
  const isFiltered = filterValue != null && (
    filterValue.condition || filterValue.text || (filterValue.checkedValues && filterValue.checkedValues.length > 0)
  )

  const [filterOpen, setFilterOpen] = useState(false)
  const [anchorRect, setAnchorRect] = useState(null)
  const filterBtnRef = useRef(null)

  return (
    <th
      draggable={!isPinned}
      onDragStart={e => onDragStart(e, column.id)}
      onDragOver={onDragOver}
      onDrop={e => onDrop(e, column.id)}
      style={{ width: header.getSize(), minWidth: column.columnDef.minSize ?? 40 }}
      className={`relative px-3 py-2 text-left text-xs font-medium text-gray-500 select-none whitespace-nowrap border-r border-gray-200 last:border-r-0 ${
        isPinned ? 'bg-gray-100 sticky z-20' : 'bg-gray-50'
      } ${!isPinned ? 'cursor-grab active:cursor-grabbing' : ''}`}
    >
      <div className="flex items-center gap-1">
        <div
          className={`flex items-center flex-1 min-w-0 ${canSort ? 'cursor-pointer' : ''}`}
          onClick={canSort ? column.getToggleSortingHandler() : undefined}
        >
          <span className="truncate">{flexRender(column.columnDef.header, header.getContext())}</span>
          {canSort && <SortIcon direction={sortDir} />}
        </div>
        {canFilter && (
          <button
            ref={filterBtnRef}
            onClick={e => {
              e.stopPropagation()
              if (!filterOpen && filterBtnRef.current) {
                setAnchorRect(filterBtnRef.current.getBoundingClientRect())
              }
              setFilterOpen(prev => !prev)
            }}
            className="p-0.5 rounded hover:bg-gray-200"
            title="필터"
          >
            <FilterIcon active={isFiltered} />
          </button>
        )}
      </div>

      {/* 필터 팝업 */}
      {filterOpen && anchorRect && (
        <ColumnFilterPopup
          column={column}
          data={data}
          anchorRect={anchorRect}
          onClose={() => setFilterOpen(false)}
        />
      )}

      <ResizeHandle header={header} />
    </th>
  )
}

/* ── 페이징 컨트롤 ─────────────────────────────── */
function Pagination({ page, totalPages, totalElements, pageSize, onPageChange }) {
  if (totalPages <= 0) return null

  const pages = useMemo(() => {
    const items = []
    const maxVisible = 5
    let start = Math.max(0, page - Math.floor(maxVisible / 2))
    let end = Math.min(totalPages, start + maxVisible)
    if (end - start < maxVisible) start = Math.max(0, end - maxVisible)
    for (let i = start; i < end; i++) items.push(i)
    return items
  }, [page, totalPages])

  return (
    <div className="flex items-center justify-between px-3 py-2 border-t border-gray-200 bg-gray-50 text-xs text-gray-600">
      <span>총 {totalElements}건 ({pageSize}건씩)</span>
      <div className="flex items-center gap-1">
        <button
          onClick={() => onPageChange(0)}
          disabled={page === 0}
          className="px-2 py-1 rounded border border-gray-300 disabled:opacity-40 hover:bg-gray-100"
        >«</button>
        <button
          onClick={() => onPageChange(page - 1)}
          disabled={page === 0}
          className="px-2 py-1 rounded border border-gray-300 disabled:opacity-40 hover:bg-gray-100"
        >‹</button>
        {pages.map(p => (
          <button
            key={p}
            onClick={() => onPageChange(p)}
            className={`px-2.5 py-1 rounded border ${
              p === page ? 'bg-blue-600 text-white border-blue-600' : 'border-gray-300 hover:bg-gray-100'
            }`}
          >{p + 1}</button>
        ))}
        <button
          onClick={() => onPageChange(page + 1)}
          disabled={page >= totalPages - 1}
          className="px-2 py-1 rounded border border-gray-300 disabled:opacity-40 hover:bg-gray-100"
        >›</button>
        <button
          onClick={() => onPageChange(totalPages - 1)}
          disabled={page >= totalPages - 1}
          className="px-2 py-1 rounded border border-gray-300 disabled:opacity-40 hover:bg-gray-100"
        >»</button>
      </div>
    </div>
  )
}

/* ══════════════════════════════════════════════════
   DataGrid (공통 컴포넌트)
   ══════════════════════════════════════════════════ */
/* ── 톱니바퀴 아이콘 ──────────────────────────── */
function SettingsIcon() {
  return (
    <svg viewBox="0 0 20 20" fill="currentColor" className="w-4 h-4">
      <path fillRule="evenodd" d="M11.49 3.17c-.38-1.56-2.6-1.56-2.98 0a1.532 1.532 0 01-2.286.948c-1.372-.836-2.942.734-2.106 2.106.54.886.061 2.042-.947 2.287-1.561.379-1.561 2.6 0 2.978a1.532 1.532 0 01.947 2.287c-.836 1.372.734 2.942 2.106 2.106a1.532 1.532 0 012.287.947c.379 1.561 2.6 1.561 2.978 0a1.533 1.533 0 012.287-.947c1.372.836 2.942-.734 2.106-2.106a1.533 1.533 0 01.947-2.287c1.561-.379 1.561-2.6 0-2.978a1.532 1.532 0 01-.947-2.287c.836-1.372-.734-2.942-2.106-2.106a1.532 1.532 0 01-2.287-.947zM10 13a3 3 0 100-6 3 3 0 000 6z" clipRule="evenodd" />
    </svg>
  )
}

/* ── localStorage 헬퍼 ────────────────────────── */
function loadColumnSettings(storageKey) {
  if (!storageKey) return null
  try {
    const raw = localStorage.getItem(`datagrid_col_${storageKey}`)
    return raw ? JSON.parse(raw) : null
  } catch { return null }
}

function saveColumnSettings(storageKey, order, hidden) {
  if (!storageKey) return
  localStorage.setItem(`datagrid_col_${storageKey}`, JSON.stringify({
    order,
    hidden: [...hidden],
  }))
}

export default function DataGrid({
  columns: columnsProp,
  data,
  pinnedCount = 0,
  page = 0,
  totalPages = 0,
  totalElements = 0,
  pageSize = 10,
  onPageChange,
  onRowClick,
  selectedRowId,
  rowIdAccessor = 'id',
  emptyMessage = '데이터가 없습니다.',
  sorting: externalSorting,
  onSortingChange: externalOnSortingChange,
  storageKey,
  title,
}) {
  /* 기본 컬럼 ID 순서 */
  const defaultOrder = useMemo(
    () => columnsProp.map(c => c.accessorKey ?? c.id),
    [columnsProp]
  )

  /* localStorage에서 설정 복원 */
  const [savedSettings] = useState(() => loadColumnSettings(storageKey))
  const [hiddenColumns, setHiddenColumns] = useState(
    () => new Set(savedSettings?.hidden ?? [])
  )
  const [userColumnOrder, setUserColumnOrder] = useState(
    () => {
      if (savedSettings?.order) {
        // 새로 추가된 컬럼이 있으면 뒤에 추가
        const saved = savedSettings.order.filter(id => defaultOrder.includes(id))
        const newCols = defaultOrder.filter(id => !saved.includes(id))
        return [...saved, ...newCols]
      }
      return defaultOrder
    }
  )

  /* 가시성 필터링된 컬럼에 advancedFilterFn 주입 */
  const columns = useMemo(
    () => columnsProp
      .filter(col => !hiddenColumns.has(col.accessorKey ?? col.id))
      .map(col => ({ ...col, filterFn: col.filterFn ?? advancedFilterFn })),
    [columnsProp, hiddenColumns]
  )

  /* 내부 정렬 상태 */
  const [internalSorting, setInternalSorting] = useState([])
  const sorting = externalSorting ?? internalSorting
  const onSortingChange = externalOnSortingChange ?? setInternalSorting

  const [columnFilters, setColumnFilters] = useState([])

  /* 컬럼 순서: 숨겨진 컬럼 제거한 순서 */
  const columnOrder = useMemo(
    () => userColumnOrder.filter(id => !hiddenColumns.has(id)),
    [userColumnOrder, hiddenColumns]
  )
  const setColumnOrder = useCallback((updater) => {
    const newOrder = typeof updater === 'function' ? updater(userColumnOrder) : updater
    setUserColumnOrder(newOrder)
  }, [userColumnOrder])

  const dragColRef = useRef(null)

  /* 컬럼 설정 팝업 상태 */
  const [settingsOpen, setSettingsOpen] = useState(false)
  const [settingsAnchorRect, setSettingsAnchorRect] = useState(null)
  const settingsBtnRef = useRef(null)

  /* 설정 적용 콜백 */
  const handleSettingsApply = useCallback((newOrder, newHidden) => {
    setUserColumnOrder(newOrder)
    setHiddenColumns(newHidden)
    saveColumnSettings(storageKey, newOrder, newHidden)
  }, [storageKey])

  /* 초기화 콜백 */
  const handleSettingsReset = useCallback(() => {
    setUserColumnOrder(defaultOrder)
    setHiddenColumns(new Set())
    if (storageKey) localStorage.removeItem(`datagrid_col_${storageKey}`)
  }, [defaultOrder, storageKey])

  const table = useReactTable({
    data,
    columns,
    state: { sorting, columnFilters, columnOrder },
    onSortingChange,
    onColumnFiltersChange: setColumnFilters,
    onColumnOrderChange: setColumnOrder,
    getCoreRowModel: getCoreRowModel(),
    getSortedRowModel: getSortedRowModel(),
    getFilteredRowModel: getFilteredRowModel(),
    columnResizeMode: 'onChange',
    enableColumnResizing: true,
  })

  /* 드래그 핸들러 */
  const onDragStart = useCallback((e, colId) => {
    dragColRef.current = colId
    e.dataTransfer.effectAllowed = 'move'
  }, [])

  const onDragOver = useCallback(e => {
    e.preventDefault()
    e.dataTransfer.dropEffect = 'move'
  }, [])

  const onDrop = useCallback((e, targetColId) => {
    e.preventDefault()
    const sourceColId = dragColRef.current
    if (!sourceColId || sourceColId === targetColId) return

    setColumnOrder(prev => {
      const next = [...prev]
      const srcIdx = next.indexOf(sourceColId)
      const tgtIdx = next.indexOf(targetColId)
      if (srcIdx === -1 || tgtIdx === -1) return prev
      if (tgtIdx < pinnedCount) return prev
      next.splice(srcIdx, 1)
      next.splice(tgtIdx, 0, sourceColId)
      return next
    })
    dragColRef.current = null
  }, [pinnedCount])

  const headerGroups = table.getHeaderGroups()
  const rows = table.getRowModel().rows

  const getPinnedLeft = (visibleIdx) => {
    if (visibleIdx >= pinnedCount) return undefined
    let left = 0
    const headers = headerGroups[0]?.headers ?? []
    for (let i = 0; i < visibleIdx; i++) {
      left += headers[i]?.getSize() ?? 0
    }
    return left
  }

  const totalWidth = headerGroups[0]?.headers.reduce((sum, h) => sum + h.getSize(), 0) ?? 0

  return (
    <div className="bg-white rounded-lg shadow-sm border border-gray-200 overflow-hidden">
      {/* 툴바: 타이틀 + 컬럼 설정 */}
      {(title || storageKey) && (
        <div className="flex items-center justify-between px-3 py-1.5 border-b border-gray-100">
          {title ? <span className="text-xs font-semibold text-gray-700">{title}</span> : <span />}
          {storageKey && (
            <>
              <button
                ref={settingsBtnRef}
                onClick={() => {
                  if (!settingsOpen && settingsBtnRef.current) {
                    setSettingsAnchorRect(settingsBtnRef.current.getBoundingClientRect())
                  }
                  setSettingsOpen(prev => !prev)
                }}
                className="p-1 rounded text-gray-400 hover:text-gray-600 hover:bg-gray-100"
                title="컬럼 설정"
              >
                <SettingsIcon />
              </button>
              {settingsOpen && settingsAnchorRect && (
                <ColumnSettingsPopup
                  columns={columnsProp}
                  columnOrder={userColumnOrder}
                  hiddenColumns={hiddenColumns}
                  onApply={handleSettingsApply}
                  onReset={handleSettingsReset}
                  onClose={() => setSettingsOpen(false)}
                  anchorRect={settingsAnchorRect}
                />
              )}
            </>
          )}
        </div>
      )}
      <div className="overflow-x-auto">
        <table style={{ tableLayout: 'fixed', width: totalWidth }} className="text-sm">
          <colgroup>
            {headerGroups[0]?.headers.map(header => (
              <col key={header.id} style={{ width: header.getSize() }} />
            ))}
          </colgroup>

          {/* 헤더 */}
          <thead className="border-b border-gray-200">
            {headerGroups.map(headerGroup => (
              <tr key={headerGroup.id}>
                {headerGroup.headers.map((header, idx) => {
                  const isPinned = idx < pinnedCount
                  return (
                    <DraggableHeader
                      key={header.id}
                      header={header}
                      isPinned={isPinned}
                      onDragStart={onDragStart}
                      onDragOver={onDragOver}
                      onDrop={onDrop}
                      data={data}
                    />
                  )
                })}
              </tr>
            ))}
          </thead>

          {/* 바디 */}
          <tbody>
            {rows.length === 0 ? (
              <tr>
                <td colSpan={columns.length} className="px-4 py-6 text-center text-xs text-gray-400">
                  {emptyMessage}
                </td>
              </tr>
            ) : (
              rows.map(row => {
                const rowId = row.original[rowIdAccessor]
                const isSelected = selectedRowId != null && rowId === selectedRowId
                return (
                  <tr
                    key={row.id}
                    onClick={() => onRowClick?.(row.original)}
                    className={`h-7 border-b border-gray-100 cursor-pointer ${
                      isSelected ? 'bg-blue-50' : 'hover:bg-gray-50'
                    }`}
                  >
                    {row.getVisibleCells().map((cell, idx) => {
                      const isPinned = idx < pinnedCount
                      return (
                        <td
                          key={cell.id}
                          className={`px-3 text-xs text-gray-700 truncate border-r border-gray-100 last:border-r-0 ${
                            isPinned ? 'sticky z-10 bg-white' : ''
                          } ${isSelected && isPinned ? '!bg-blue-50' : ''}`}
                          style={isPinned ? { left: getPinnedLeft(idx) } : undefined}
                        >
                          {flexRender(cell.column.columnDef.cell, cell.getContext())}
                        </td>
                      )
                    })}
                  </tr>
                )
              })
            )}
          </tbody>
        </table>
      </div>

      {/* 페이징 */}
      {onPageChange && (
        <Pagination
          page={page}
          totalPages={totalPages}
          totalElements={totalElements}
          pageSize={pageSize}
          onPageChange={onPageChange}
        />
      )}
    </div>
  )
}
