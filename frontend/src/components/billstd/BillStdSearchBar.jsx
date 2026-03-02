export default function BillStdSearchBar({ keyword, onKeywordChange, searchType, onSearchTypeChange, onSearch, isSearching }) {
  const placeholder = searchType === 'subsId' ? '가입ID 입력' : '과금기준ID 입력'

  return (
    <div className="bg-white rounded-lg shadow-sm border border-gray-100 p-4">
      <div className="flex items-center gap-3">
        <label className="w-24 shrink-0 text-right text-sm text-gray-500">검색 유형</label>
        <select
          className="h-8 w-40 border border-gray-300 rounded-md px-3 text-sm text-gray-700
                     focus:outline-none focus:ring-2 focus:ring-blue-600 focus:border-blue-600"
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
          className="flex-1 h-8 border border-gray-300 rounded-md px-3 text-sm
                     focus:outline-none focus:ring-2 focus:ring-blue-600 focus:border-blue-600"
        />

        <button
          onClick={onSearch}
          disabled={isSearching}
          className="h-8 px-5 bg-[#2563EB] hover:bg-blue-700 disabled:opacity-50
                     text-white text-sm font-medium rounded-md transition-colors shrink-0"
        >
          {isSearching ? '조회 중...' : '조회'}
        </button>
      </div>
    </div>
  )
}
