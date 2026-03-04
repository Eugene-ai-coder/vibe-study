const SVC_OPTIONS = ['전체', 'IDC 전력', 'IDC NW', '비즈넷']
const SEARCH_TYPE_OPTIONS = ['서비스', '상품', '가입ID', '대표가입ID']

export default function SubscriptionMainSearchBar({
  svcNm, onSvcNmChange,
  searchType, onSearchTypeChange,
  keyword, onKeywordChange,
  onSearch, isLoading,
}) {
  const handleKeyDown = (e) => {
    if (e.key === 'Enter') onSearch()
  }

  return (
    <div className="bg-gray-50 border border-gray-200 rounded-lg p-3">
      <div className="flex items-center gap-2">
        <label className="text-xs text-gray-500 whitespace-nowrap">서비스</label>
        <select
          value={svcNm}
          onChange={onSvcNmChange}
          className="h-8 border border-gray-300 rounded px-2 text-sm focus:outline-none focus:border-blue-400"
        >
          {SVC_OPTIONS.map(opt => (
            <option key={opt} value={opt}>{opt}</option>
          ))}
        </select>

        <label className="text-xs text-gray-500 whitespace-nowrap">조회조건</label>
        <select
          value={searchType}
          onChange={onSearchTypeChange}
          className="h-8 border border-gray-300 rounded px-2 text-sm focus:outline-none focus:border-blue-400"
        >
          {SEARCH_TYPE_OPTIONS.map(opt => (
            <option key={opt} value={opt}>{opt}</option>
          ))}
        </select>

        <input
          type="text"
          value={keyword}
          onChange={onKeywordChange}
          onKeyDown={handleKeyDown}
          placeholder="2자 이상 입력"
          className="w-48 h-8 border border-gray-300 rounded px-2 text-sm focus:outline-none focus:border-blue-400"
        />

        <button
          onClick={onSearch}
          disabled={isLoading}
          className="h-8 px-4 bg-blue-600 text-white text-sm rounded hover:bg-blue-700 disabled:opacity-50 whitespace-nowrap"
        >
          조회
        </button>
      </div>
    </div>
  )
}
