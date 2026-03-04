function Field({ label, name, value, readOnly = false, required = false, onChange, options }) {
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
      {options ? (
        <select name={name} value={value} onChange={onChange} disabled={readOnly} className={inputClass}>
          {options.map((opt) => (
            <option key={opt} value={opt}>{opt}</option>
          ))}
        </select>
      ) : (
        <input
          name={name}
          value={value}
          readOnly={readOnly}
          onChange={onChange}
          className={inputClass}
        />
      )}
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

export default function BillStdForm({ data = {}, onChange }) {
  const handleChange = onChange ?? (() => {})

  return (
    <div className="space-y-4">

      <Section title="기본 정보">
        <Field label="과금기준ID"   name="billStdId"     value={data.billStdId     || ''} readOnly />
        <Field label="가입ID"       name="subsId"        value={data.subsId        || ''} onChange={handleChange} required />
        <Field label="등록일시"     name="billStdRegDt"  value={data.billStdRegDt  || ''} onChange={handleChange} />
        <Field label="서비스코드"   name="svcCd"         value={data.svcCd         || ''} onChange={handleChange} required />
        <Field label="최종유효여부" name="lastEffYn"     value={data.lastEffYn     || 'Y'} onChange={handleChange} required options={['Y', 'N']} />
        <Field label="유효시작일시" name="effStartDt"    value={data.effStartDt    || ''} onChange={handleChange} required />
        <Field label="유효종료일시" name="effEndDt"      value={data.effEndDt      || ''} onChange={handleChange} />
        <Field label="등록진행상태" name="stdRegStatCd"  value={data.stdRegStatCd  || ''} onChange={handleChange} required />
        <Field label="과금기준상태" name="billStdStatCd" value={data.billStdStatCd || ''} onChange={handleChange} required />
      </Section>

      <Section title="단가 설정">
        <Field label="전력종량방식" name="pwrMetCalcMethCd"     value={data.pwrMetCalcMethCd     || ''} onChange={handleChange} />
        <Field label="단가결정방식" name="uprcDetMethCd"        value={data.uprcDetMethCd        || ''} onChange={handleChange} />
        <Field label="종량단가"     name="meteringUnitPriceAmt" value={data.meteringUnitPriceAmt || ''} onChange={handleChange} />
        <Field label="과금량갯수"   name="billQty"              value={data.billQty              || ''} onChange={handleChange} />
      </Section>

      <Section title="PUE">
        <Field label="PUE결정방식" name="pueDetMethCd" value={data.pueDetMethCd || ''} onChange={handleChange} />
        <Field label="PUE1"        name="pue1Rt"       value={data.pue1Rt       || ''} onChange={handleChange} />
        <Field label="PUE2"        name="pue2Rt"       value={data.pue2Rt       || ''} onChange={handleChange} />
      </Section>

      <Section title="약정 및 할인">
        <Field label="1차할인율"     name="firstDscRt"     value={data.firstDscRt     || ''} onChange={handleChange} />
        <Field label="2차할인율"     name="secondDscRt"    value={data.secondDscRt    || ''} onChange={handleChange} />
        <Field label="손실보상율"    name="lossCompRt"     value={data.lossCompRt     || ''} onChange={handleChange} />
        <Field label="약정용량(Kwh)" name="cntrcCapKmh"    value={data.cntrcCapKmh    || ''} onChange={handleChange} />
        <Field label="약정요금"      name="cntrcAmt"       value={data.cntrcAmt       || ''} onChange={handleChange} />
        <Field label="할인액"        name="dscAmt"         value={data.dscAmt         || ''} onChange={handleChange} />
        <Field label="일별단가"      name="dailyUnitPrice" value={data.dailyUnitPrice || ''} onChange={handleChange} />
      </Section>

    </div>
  )
}
