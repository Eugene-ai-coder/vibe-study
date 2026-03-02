const EXAMPLE = {
  billStdId:            'BS-2024-0001',
  subsId:               'SUB-001',
  billStdRegDt:         '2024-01-15 09:00:00',
  svcCd:                'SVC01',
  lastEffYn:            'Y',
  effStartDt:           '2024-01-01 00:00:00',
  effEndDt:             '2024-12-31 23:59:59',
  stdRegStatCd:         'APRV',
  billStdStatCd:        'ACTV',
  pwrMetCalcMethCd:     'MTRG',
  uprcDetMethCd:        'FIX',
  meteringUnitPriceAmt: '120.50',
  billQty:              '1000',
  pueDetMethCd:         'CALC',
  pue1Rt:               '1.45',
  pue2Rt:               '1.30',
  firstDscRt:           '5.00',
  secondDscRt:          '3.00',
  lossCompRt:           '2.50',
  cntrcCapKmh:          '500',
  cntrcAmt:             '1,200,000',
  dscAmt:               '60,000',
  dailyUnitPrice:       '4,000',
}

function Field({ label, name, value, readOnly = false, required = false, onChange, options }) {
  const inputClass = [
    'flex-1 h-8 border rounded-md px-3 text-sm transition-colors',
    readOnly
      ? 'bg-gray-50 text-gray-400 cursor-default border-gray-300'
      : 'bg-white border-gray-300 focus:outline-none focus:ring-2 focus:ring-blue-600 focus:border-blue-600',
  ].join(' ')

  return (
    <div className="flex flex-col sm:flex-row sm:items-center gap-1 sm:gap-2">
      <label className="sm:w-24 sm:shrink-0 sm:text-right text-sm text-gray-500 flex items-center sm:justify-end gap-0.5">
        {label}
        {required && <span className="text-red-500 text-xs">*</span>}
      </label>
      {options ? (
        <select name={name} value={value} onChange={onChange} className={inputClass}>
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

export default function BillStdForm({ data = EXAMPLE, onChange }) {
  const handleChange = onChange ?? (() => {})

  return (
    <div className="bg-white rounded-lg shadow-sm border border-gray-100 p-6">
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-x-6 gap-y-4">

        <Field label="과금기준ID"    name="billStdId"            value={data.billStdId}            readOnly />
        <Field label="가입ID"        name="subsId"               value={data.subsId}               onChange={handleChange} required />
        <Field label="등록일시"      name="billStdRegDt"         value={data.billStdRegDt}         onChange={handleChange} />

        <Field label="서비스코드"    name="svcCd"                value={data.svcCd}                onChange={handleChange} required />
        <Field label="최종유효여부"  name="lastEffYn"            value={data.lastEffYn}            onChange={handleChange} required options={['Y', 'N']} />
        <Field label="유효시작일시"  name="effStartDt"           value={data.effStartDt}           onChange={handleChange} required />

        <Field label="유효종료일시"  name="effEndDt"             value={data.effEndDt}             onChange={handleChange} />
        <Field label="등록진행상태"  name="stdRegStatCd"         value={data.stdRegStatCd}         onChange={handleChange} required />
        <Field label="과금기준상태"  name="billStdStatCd"        value={data.billStdStatCd}        onChange={handleChange} required />

        <Field label="전력종량방식"  name="pwrMetCalcMethCd"     value={data.pwrMetCalcMethCd}     onChange={handleChange} />
        <Field label="단가결정방식"  name="uprcDetMethCd"        value={data.uprcDetMethCd}        onChange={handleChange} />
        <Field label="종량단가"      name="meteringUnitPriceAmt" value={data.meteringUnitPriceAmt} onChange={handleChange} />

        <Field label="과금량갯수"    name="billQty"              value={data.billQty}              onChange={handleChange} />
        <Field label="PUE결정방식"   name="pueDetMethCd"         value={data.pueDetMethCd}         onChange={handleChange} />
        <Field label="PUE1"          name="pue1Rt"               value={data.pue1Rt}               onChange={handleChange} />

        <Field label="PUE2"          name="pue2Rt"               value={data.pue2Rt}               onChange={handleChange} />
        <Field label="1차할인율"     name="firstDscRt"           value={data.firstDscRt}           onChange={handleChange} />
        <Field label="2차할인율"     name="secondDscRt"          value={data.secondDscRt}          onChange={handleChange} />

        <Field label="손실보상율"    name="lossCompRt"           value={data.lossCompRt}           onChange={handleChange} />
        <Field label="약정용량(Kwh)" name="cntrcCapKmh"          value={data.cntrcCapKmh}          onChange={handleChange} />
        <Field label="약정요금"      name="cntrcAmt"             value={data.cntrcAmt}             onChange={handleChange} />

        <Field label="할인액"        name="dscAmt"               value={data.dscAmt}               onChange={handleChange} />
        <Field label="일별단가"      name="dailyUnitPrice"       value={data.dailyUnitPrice}       onChange={handleChange} />

      </div>
    </div>
  )
}
