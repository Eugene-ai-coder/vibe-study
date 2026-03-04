import { useState, useRef, useCallback, useMemo, useEffect } from 'react'

const DEFAULT_WIDTHS = { subsId: 160, svcNm: 120, feeProdNm: 160, mainSubsYn: 80, mainSubsId: 160 }
const COLS = ['subsId', 'svcNm', 'feeProdNm', 'mainSubsYn', 'mainSubsId']
const HEADERS = { subsId: '가입ID', svcNm: '서비스', feeProdNm: '상품', mainSubsYn: '대표가입여부', mainSubsId: '대표가입ID' }

export default function SubscriptionMainList({ items, selectedId, onRowClick }) {
  const [colWidths, setColWidths] = useState(DEFAULT_WIDTHS)
  const dragging = useRef(null)
  const cleanupRef = useRef(null)

  useEffect(() => () => { if (cleanupRef.current) cleanupRef.current() }, [])

  const onMouseDown = useCallback((col, e) => {
    e.preventDefault()
    dragging.current = { col, startX: e.clientX, startW: colWidths[col] }
    const onMove = (ev) => {
      if (!dragging.current) return
      const delta = ev.clientX - dragging.current.startX
      setColWidths(prev => ({
        ...prev,
        [dragging.current.col]: Math.max(60, dragging.current.startW + delta)
      }))
    }
    const onUp = () => {
      dragging.current = null
      cleanupRef.current = null
      window.removeEventListener('mousemove', onMove)
      window.removeEventListener('mouseup', onUp)
    }
    cleanupRef.current = () => {
      window.removeEventListener('mousemove', onMove)
      window.removeEventListener('mouseup', onUp)
    }
    window.addEventListener('mousemove', onMove)
    window.addEventListener('mouseup', onUp)
  }, [colWidths])

  const totalWidth = useMemo(() => COLS.reduce((sum, col) => sum + colWidths[col], 0), [colWidths])

  return (
    <div className="bg-white rounded-lg shadow-sm border border-gray-200 overflow-hidden">
      <div className="overflow-x-auto">
        <table className="text-sm border-collapse" style={{ tableLayout: 'fixed', width: totalWidth }}>
          <colgroup>
            {COLS.map(col => <col key={col} style={{ width: colWidths[col] }} />)}
          </colgroup>
          <thead className="bg-gray-50 border-t border-gray-300 border-b border-gray-300">
            <tr>
              {COLS.map(col => (
                <th key={col} style={{ width: colWidths[col] }}
                  className="relative px-3 py-1.5 text-left text-xs font-medium text-gray-500">
                  {HEADERS[col]}
                  <span
                    onMouseDown={(e) => onMouseDown(col, e)}
                    className="absolute right-0 top-0 h-full w-1 cursor-col-resize bg-gray-200 hover:bg-blue-400"
                  />
                </th>
              ))}
            </tr>
          </thead>
        </table>
        <div className="h-[280px] overflow-y-auto overflow-x-auto">
          <table className="text-sm border-collapse" style={{ tableLayout: 'fixed', width: totalWidth }}>
            <colgroup>
              {COLS.map(col => <col key={col} style={{ width: colWidths[col] }} />)}
            </colgroup>
            <tbody>
              {items.length === 0 ? (
                <tr>
                  <td colSpan={5} className="px-3 py-4 text-center text-xs text-gray-400">
                    조회 결과가 없습니다.
                  </td>
                </tr>
              ) : (
                items.map((item) => {
                  const isSelected = item.subsId === selectedId
                  return (
                    <tr
                      key={item.subsId}
                      onClick={() => onRowClick(item)}
                      className={`h-7 cursor-pointer border-b border-gray-200 ${
                        isSelected
                          ? 'bg-blue-50 text-blue-900'
                          : 'hover:bg-gray-50 text-gray-800'
                      }`}
                    >
                      <td className="px-3 text-xs truncate">{item.subsId}</td>
                      <td className="px-3 text-xs truncate">{item.svcNm}</td>
                      <td className="px-3 text-xs truncate">{item.feeProdNm}</td>
                      <td className="px-3 text-xs text-center">{item.mainSubsYn}</td>
                      <td className="px-3 text-xs truncate">{item.mainSubsId || '-'}</td>
                    </tr>
                  )
                })
              )}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  )
}
