export default function SubscriptionForm({ data, onChange, isReadOnly }) {
  return (
    <div className="bg-white rounded-lg shadow-sm p-4">
      <div className="grid grid-cols-3 gap-4">
        <div>
          <label className="block text-xs text-gray-500 mb-1">가입ID <span className="text-red-500">*</span></label>
          <input
            name="subsId"
            value={data.subsId}
            onChange={onChange}
            readOnly={isReadOnly}
            className={`w-full h-10 border border-gray-300 rounded-lg px-3 text-sm
              ${isReadOnly ? 'bg-gray-100 text-gray-500' : ''}`}
          />
        </div>
        <div>
          <label className="block text-xs text-gray-500 mb-1">가입자명</label>
          <input name="subsNm" value={data.subsNm} onChange={onChange}
            className="w-full h-10 border border-gray-300 rounded-lg px-3 text-sm" />
        </div>
        <div>
          <label className="block text-xs text-gray-500 mb-1">서비스명</label>
          <input name="svcNm" value={data.svcNm} onChange={onChange}
            className="w-full h-10 border border-gray-300 rounded-lg px-3 text-sm" />
        </div>
        <div>
          <label className="block text-xs text-gray-500 mb-1">요금상품명</label>
          <input name="feeProdNm" value={data.feeProdNm} onChange={onChange}
            className="w-full h-10 border border-gray-300 rounded-lg px-3 text-sm" />
        </div>
        <div>
          <label className="block text-xs text-gray-500 mb-1">가입상태코드</label>
          <select name="subsStatusCd" value={data.subsStatusCd} onChange={onChange}
            className="w-full h-10 border border-gray-300 rounded-lg px-3 text-sm">
            <option value="">선택</option>
            <option value="ACTIVE">ACTIVE (활성)</option>
            <option value="SUSPENDED">SUSPENDED (정지)</option>
            <option value="TERMINATED">TERMINATED (해지)</option>
            <option value="PENDING">PENDING (가입대기)</option>
          </select>
        </div>
        <div>
          <label className="block text-xs text-gray-500 mb-1">가입일시</label>
          <input name="subsDt" type="datetime-local" value={data.subsDt} onChange={onChange}
            className="w-full h-10 border border-gray-300 rounded-lg px-3 text-sm" />
        </div>
        <div>
          <label className="block text-xs text-gray-500 mb-1">변경일시</label>
          <input name="chgDt" type="datetime-local" value={data.chgDt} onChange={onChange}
            className="w-full h-10 border border-gray-300 rounded-lg px-3 text-sm" />
        </div>
      </div>
    </div>
  )
}
