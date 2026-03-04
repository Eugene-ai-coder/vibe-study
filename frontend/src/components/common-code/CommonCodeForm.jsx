export default function CommonCodeForm({ data, onChange, isEdit }) {
  return (
    <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-4">
      <h2 className="text-sm font-semibold text-gray-700 mb-3">공통코드 그룹 정보</h2>
      <div className="grid grid-cols-6 gap-x-4 gap-y-3">
        <label className="col-span-1 flex items-center text-xs text-gray-500">공통코드</label>
        <div className="col-span-1">
          <input
            name="commonCode"
            value={data.commonCode || ''}
            onChange={onChange}
            readOnly={isEdit}
            className={`w-full h-8 border rounded px-2 text-sm focus:outline-none focus:border-blue-400 ${
              isEdit ? 'border-gray-200 bg-gray-50 text-gray-500' : 'border-gray-300'
            }`}
          />
        </div>
        <label className="col-span-1 flex items-center text-xs text-gray-500">공통코드명</label>
        <div className="col-span-1">
          <input
            name="commonCodeNm"
            value={data.commonCodeNm || ''}
            onChange={onChange}
            className="w-full h-8 border border-gray-300 rounded px-2 text-sm focus:outline-none focus:border-blue-400"
          />
        </div>
        <label className="col-span-1 flex items-center text-xs text-gray-500">유효시작일</label>
        <div className="col-span-1">
          <input
            type="datetime-local"
            name="effStartDt"
            value={data.effStartDt || ''}
            onChange={onChange}
            className="w-full h-8 border border-gray-300 rounded px-2 text-sm focus:outline-none focus:border-blue-400"
          />
        </div>

        <label className="col-span-1 flex items-center text-xs text-gray-500">유효종료일</label>
        <div className="col-span-1">
          <input
            type="datetime-local"
            name="effEndDt"
            value={data.effEndDt || ''}
            onChange={onChange}
            className="w-full h-8 border border-gray-300 rounded px-2 text-sm focus:outline-none focus:border-blue-400"
          />
        </div>
        <label className="col-span-1 flex items-center text-xs text-gray-500">비고</label>
        <div className="col-span-3">
          <textarea
            name="remark"
            value={data.remark || ''}
            onChange={onChange}
            rows={2}
            className="w-full border border-gray-300 rounded px-2 py-1 text-sm focus:outline-none focus:border-blue-400 resize-none"
          />
        </div>
      </div>
    </div>
  )
}
