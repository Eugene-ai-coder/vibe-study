import { useState, useMemo } from 'react'

const STATUS_LABEL = {
  ACTIVE: '활성', SUSPENDED: '정지', TERMINATED: '해지', PENDING: '가입대기'
}

export default function SubscriptionList({ items, selectedId, onSelect }) {
  const [filter, setFilter] = useState({ subsId: '', subsNm: '', svcNm: '', subsStatusCd: '' })

  const filtered = useMemo(() => items.filter(item =>
    item.subsId.toLowerCase().includes(filter.subsId.toLowerCase()) &&
    (item.subsNm || '').toLowerCase().includes(filter.subsNm.toLowerCase()) &&
    (item.svcNm || '').toLowerCase().includes(filter.svcNm.toLowerCase()) &&
    (item.subsStatusCd || '').toLowerCase().includes(filter.subsStatusCd.toLowerCase())
  ), [items, filter])

  const handleFilter = (col, val) => setFilter(prev => ({ ...prev, [col]: val }))

  return (
    <div className="bg-white rounded-lg shadow-sm overflow-hidden">
      <table className="w-full text-sm">
        <thead className="bg-gray-50 border-b border-gray-200">
          <tr>
            {['가입ID', '가입자명', '서비스명', '가입상태'].map(col => (
              <th key={col} className="text-left px-4 py-2 text-gray-600 font-medium">{col}</th>
            ))}
          </tr>
          <tr>
            {['subsId', 'subsNm', 'svcNm', 'subsStatusCd'].map(col => (
              <th key={col} className="px-4 py-1">
                <input
                  type="text"
                  value={filter[col]}
                  onChange={(e) => handleFilter(col, e.target.value)}
                  placeholder="필터"
                  className="w-full h-7 border border-gray-200 rounded px-2 text-xs font-normal"
                />
              </th>
            ))}
          </tr>
        </thead>
        <tbody>
          {filtered.length === 0 ? (
            <tr><td colSpan={4} className="text-center py-8 text-gray-400">데이터가 없습니다.</td></tr>
          ) : (
            filtered.map(item => (
              <tr
                key={item.subsId}
                onClick={() => onSelect(item)}
                className={`cursor-pointer border-b border-gray-100 hover:bg-blue-50
                  ${selectedId === item.subsId ? 'bg-blue-50 font-medium' : ''}`}
              >
                <td className="px-4 py-2">{item.subsId}</td>
                <td className="px-4 py-2">{item.subsNm}</td>
                <td className="px-4 py-2">{item.svcNm}</td>
                <td className="px-4 py-2">
                  <span className={`text-xs px-2 py-0.5 rounded-full
                    ${item.subsStatusCd === 'ACTIVE' ? 'bg-green-100 text-green-700' :
                      item.subsStatusCd === 'SUSPENDED' ? 'bg-yellow-100 text-yellow-700' :
                      item.subsStatusCd === 'TERMINATED' ? 'bg-red-100 text-red-700' :
                      'bg-gray-100 text-gray-600'}`}>
                    {STATUS_LABEL[item.subsStatusCd] || item.subsStatusCd}
                  </span>
                </td>
              </tr>
            ))
          )}
        </tbody>
      </table>
    </div>
  )
}
