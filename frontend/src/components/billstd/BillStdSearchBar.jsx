export default function BillStdSearchBar({ keyword, onKeywordChange, searchType, onSearchTypeChange, onSearch, isSearching }) {
  const placeholder = searchType === 'subsId' ? '가입ID 입력' : '과금기준ID 입력'

  return (
    <div className="bg-gray-50 border border-gray-200 rounded-lg p-3">
      <div className="flex items-center gap-2">
        <select
          className="h-8 w-36 border border-gray-300 rounded px-2 text-sm text-gray-700 focus:outline-none focus:border-blue-400"
          value={searchType}
          onChange={onSearchTypeChange}
        >
          <option value="subsId">가입ID</option>
          <option value="billStdId">과금기준ID</option>
        </select>

        <input
          type="text"
          value={keyword}
          onChange={onKeywordChange}
          onKeyDown={(e) => e.key === 'Enter' && onSearch()}
          placeholder={placeholder}
          className="w-48 h-8 border border-gray-300 rounded px-2 text-sm focus:outline-none focus:border-blue-400"
        />

        <button
          onClick={onSearch}
          disabled={isSearching}
          className="h-8 px-4 bg-blue-600 hover:bg-blue-700 disabled:opacity-50 text-white text-sm rounded transition-colors"
        >
          {isSearching ? '조회 중...' : '조회'}
        </button>
      </div>
    </div>
  )
}
