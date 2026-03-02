import { useState } from 'react'
import useSubscriptionSearch from '../../hooks/useSubscriptionSearch'

export default function SubscriptionSearchPopup({ isOpen, onClose, onSelect }) {
  const [keyword, setKeyword] = useState('')
  const { results, isSearching, search } = useSubscriptionSearch()

  if (!isOpen) return null

  const handleSearch = () => search(keyword)

  const handleKeyDown = (e) => {
    if (e.key === 'Enter') handleSearch()
  }

  const handleSelect = (subsId) => {
    onSelect(subsId)
    onClose()
  }

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/30">
      <div className="bg-white rounded-lg shadow-xl w-[500px] flex flex-col max-h-[70vh]">
        <div className="flex items-center justify-between px-4 py-3 border-b border-gray-200">
          <h2 className="text-sm font-semibold text-gray-800">가입 검색</h2>
          <button
            onClick={onClose}
            className="text-gray-400 hover:text-gray-600 text-lg leading-none"
          >
            ×
          </button>
        </div>

        <div className="flex gap-2 p-3 border-b border-gray-100">
          <input
            type="text"
            value={keyword}
            onChange={(e) => setKeyword(e.target.value)}
            onKeyDown={handleKeyDown}
            placeholder="가입ID 입력"
            className="flex-1 h-8 border border-gray-300 rounded px-2 text-sm focus:outline-none focus:border-blue-400"
          />
          <button
            onClick={handleSearch}
            disabled={isSearching}
            className="h-8 px-3 bg-[#2563EB] text-white text-sm rounded hover:bg-blue-700 disabled:opacity-50"
          >
            조회
          </button>
        </div>

        <div className="overflow-auto flex-1">
          <table className="w-full text-sm">
            <thead className="bg-gray-50 sticky top-0">
              <tr>
                <th className="px-3 py-1.5 text-left text-xs font-medium text-gray-500">가입ID</th>
                <th className="px-3 py-1.5 text-left text-xs font-medium text-gray-500">가입명</th>
                <th className="px-3 py-1.5 text-left text-xs font-medium text-gray-500">서비스</th>
              </tr>
            </thead>
            <tbody>
              {results.length === 0 ? (
                <tr>
                  <td colSpan={3} className="px-3 py-4 text-center text-xs text-gray-400">
                    조회 결과가 없습니다.
                  </td>
                </tr>
              ) : (
                results.map((item) => (
                  <tr
                    key={item.subsId}
                    onClick={() => handleSelect(item.subsId)}
                    className="h-7 cursor-pointer hover:bg-blue-50 border-b border-gray-100"
                  >
                    <td className="px-3 text-xs text-gray-800">{item.subsId}</td>
                    <td className="px-3 text-xs text-gray-600">{item.subsNm}</td>
                    <td className="px-3 text-xs text-gray-600">{item.svcNm}</td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  )
}
