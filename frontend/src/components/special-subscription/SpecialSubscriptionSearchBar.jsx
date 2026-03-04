export default function SpecialSubscriptionSearchBar({
  subsBillStdId, onSubsBillStdIdChange,
  subsId, onSubsIdChange,
  onSearch, isLoading
}) {
  return (
    <div className="bg-gray-50 border border-gray-200 rounded-lg p-3">
      <div className="flex items-center gap-2">
        <input
          type="text"
          value={subsBillStdId}
          onChange={onSubsBillStdIdChange}
          onKeyDown={(e) => e.key === 'Enter' && onSearch()}
          placeholder="가입별과금기준ID"
          className="w-48 h-8 border border-gray-300 rounded px-2 text-sm focus:outline-none focus:border-blue-400"
        />
        <input
          type="text"
          value={subsId}
          onChange={onSubsIdChange}
          onKeyDown={(e) => e.key === 'Enter' && onSearch()}
          placeholder="가입ID"
          className="w-48 h-8 border border-gray-300 rounded px-2 text-sm focus:outline-none focus:border-blue-400"
        />
        <button
          onClick={onSearch}
          disabled={isLoading}
          className="h-8 px-4 bg-blue-600 hover:bg-blue-700 disabled:opacity-50 text-white text-sm rounded transition-colors"
        >
          {isLoading ? '조회 중...' : '조회'}
        </button>
      </div>
    </div>
  )
}
