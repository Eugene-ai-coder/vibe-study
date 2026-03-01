export default function SubscriptionMainForm({ data, onChange, onOpenPopup }) {
  const handleMainSubsYnChange = (e) => {
    const value = e.target.value
    onChange({ target: { name: 'mainSubsYn', value } })
    if (value === 'Y') {
      onChange({ target: { name: 'mainSubsId', value: '' } })
    }
  }

  const isMainSubsIdActive = data.mainSubsYn === 'N'

  return (
    <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-4">
      <h2 className="text-sm font-semibold text-gray-700 mb-3">대표가입 상세</h2>
      <div className="grid grid-cols-2 gap-x-6 gap-y-3">
        <div>
          <label className="block text-xs text-gray-500 mb-1">가입ID</label>
          <input
            type="text"
            value={data.subsId || ''}
            readOnly
            className="w-full h-8 border border-gray-200 rounded px-2 text-sm bg-gray-50 text-gray-500"
          />
        </div>

        <div>
          <label className="block text-xs text-gray-500 mb-1">대표가입여부</label>
          <select
            name="mainSubsYn"
            value={data.mainSubsYn || 'Y'}
            onChange={handleMainSubsYnChange}
            disabled={!data.subsId}
            className="w-full h-8 border border-gray-300 rounded px-2 text-sm focus:outline-none focus:border-blue-400 disabled:bg-gray-50 disabled:text-gray-400"
          >
            <option value="Y">Y</option>
            <option value="N">N</option>
          </select>
        </div>

        <div>
          <label className="block text-xs text-gray-500 mb-1">유효시작일시</label>
          <input
            type="text"
            value={data.effStartDt || ''}
            readOnly
            className="w-full h-8 border border-gray-200 rounded px-2 text-sm bg-gray-50 text-gray-500"
          />
        </div>

        <div>
          <label className="block text-xs text-gray-500 mb-1">유효종료일시</label>
          <input
            type="text"
            value={data.effEndDt || ''}
            readOnly
            className="w-full h-8 border border-gray-200 rounded px-2 text-sm bg-gray-50 text-gray-500"
          />
        </div>

        <div className="col-span-2">
          <label className="block text-xs text-gray-500 mb-1">
            대표가입ID
            {isMainSubsIdActive && <span className="text-red-400 ml-1">*</span>}
          </label>
          <div className="flex gap-2">
            <input
              type="text"
              name="mainSubsId"
              value={data.mainSubsId || ''}
              onChange={onChange}
              readOnly={!isMainSubsIdActive}
              placeholder={isMainSubsIdActive ? '대표가입ID 입력 또는 검색' : ''}
              className={`flex-1 h-8 border rounded px-2 text-sm focus:outline-none focus:border-blue-400 ${
                isMainSubsIdActive
                  ? 'border-gray-300'
                  : 'border-gray-200 bg-gray-50 text-gray-400'
              }`}
            />
            <button
              onClick={onOpenPopup}
              disabled={!isMainSubsIdActive}
              className="h-8 px-3 border border-gray-300 rounded text-sm text-gray-600 hover:bg-gray-50 disabled:opacity-40 disabled:cursor-not-allowed whitespace-nowrap"
            >
              검색
            </button>
          </div>
        </div>
      </div>
    </div>
  )
}
