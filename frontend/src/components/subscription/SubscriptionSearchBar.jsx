export default function SubscriptionSearchBar({ searchType, onSearchTypeChange,
                                                keyword, onKeywordChange,
                                                onSearch, isSearching }) {
  return (
    <div className="bg-white rounded-lg shadow-sm p-4 flex items-center gap-3">
      <label className="text-sm text-gray-600 whitespace-nowrap">검색유형</label>
      <select
        value={searchType}
        onChange={onSearchTypeChange}
        className="h-10 border border-gray-300 rounded-lg px-3 text-sm"
      >
        <option value="SUBS_ID">가입ID</option>
        <option value="SVC_NM">서비스명</option>
        <option value="FEE_PROD_NM">요금상품명</option>
        <option value="SUBS_STATUS_CD">가입상태</option>
      </select>
      <input
        type="text"
        value={keyword}
        onChange={onKeywordChange}
        placeholder="검색어 입력"
        className="h-10 flex-1 border border-gray-300 rounded-lg px-3 text-sm"
        onKeyDown={(e) => e.key === 'Enter' && onSearch()}
      />
      <button
        onClick={onSearch}
        disabled={isSearching}
        className="h-10 px-6 bg-blue-600 text-white rounded-lg text-sm hover:bg-blue-700 disabled:opacity-50"
      >
        {isSearching ? '조회 중...' : '조회'}
      </button>
    </div>
  )
}
