function Field({ label, name, value, readOnly = false, required = false, onChange, type = 'text' }) {
  const inputClass = [
    'w-full h-8 border rounded px-2 text-sm',
    readOnly
      ? 'bg-gray-50 text-gray-400 border-gray-200'
      : 'bg-white border-gray-300 focus:outline-none focus:border-blue-400',
  ].join(' ')

  return (
    <div>
      <label className="block text-xs text-gray-500 mb-1">
        {label}
        {required && <span className="text-blue-400 ml-0.5">*</span>}
      </label>
      <input
        name={name}
        value={value}
        readOnly={readOnly}
        onChange={onChange}
        type={type}
        className={inputClass}
      />
    </div>
  )
}

function Section({ title, children }) {
  return (
    <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-4">
      <h3 className="text-xs font-semibold text-gray-500 uppercase tracking-wide mb-3">{title}</h3>
      <div className="grid grid-cols-3 gap-x-4 gap-y-3">
        {children}
      </div>
    </div>
  )
}

export default function SpecialSubscriptionForm({ data = {}, onChange, isNew = false }) {
  const handleChange = onChange ?? (() => {})

  return (
    <div className="space-y-4">
      <Section title="기본 정보">
        <Field label="가입별과금기준ID" name="subsBillStdId" value={data.subsBillStdId || ''} readOnly={!isNew} onChange={handleChange} required />
        <Field label="유효시작일"       name="effStaDt"      value={data.effStaDt || ''}      readOnly={!isNew} onChange={handleChange} required />
        <Field label="가입ID"          name="subsId"        value={data.subsId || ''}        onChange={handleChange} required />
        <Field label="서비스코드"       name="svcCd"         value={data.svcCd || ''}         onChange={handleChange} />
        <Field label="유효종료일"       name="effEndDt"      value={data.effEndDt || ''}      onChange={handleChange} />
        <Field label="최종유효여부"     name="lastEffYn"     value={data.lastEffYn || ''}     onChange={handleChange} />
        <Field label="상태코드"        name="statCd"        value={data.statCd || ''}        onChange={handleChange} />
      </Section>

      <Section title="약정 정보">
        <Field label="계약용량(kMh)"   name="cntrcCapKmh"  value={data.cntrcCapKmh || ''}  onChange={handleChange} />
        <Field label="계약금액"        name="cntrcAmt"     value={data.cntrcAmt || ''}     onChange={handleChange} />
        <Field label="할인율"          name="dscRt"        value={data.dscRt || ''}        onChange={handleChange} />
      </Section>

      <Section title="비고">
        <div className="col-span-3">
          <label className="block text-xs text-gray-500 mb-1">비고</label>
          <textarea
            name="rmk"
            value={data.rmk || ''}
            onChange={handleChange}
            className="w-full h-20 border border-gray-300 rounded px-2 py-1 text-sm focus:outline-none focus:border-blue-400 resize-none"
          />
        </div>
      </Section>
    </div>
  )
}
