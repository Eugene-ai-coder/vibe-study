export default function CommonDtlCodeList({ details, selectedDtl, onRowClick }) {
  return (
    <div className="bg-white rounded-lg shadow-sm border border-gray-200 overflow-hidden">
      <table className="w-full text-sm border-collapse">
        <thead className="bg-gray-50 border-t border-gray-300 border-b border-gray-300">
          <tr>
            <th className="px-3 py-1.5 text-left text-xs font-medium text-gray-500">상세코드</th>
            <th className="px-3 py-1.5 text-left text-xs font-medium text-gray-500">상세코드명</th>
            <th className="px-3 py-1.5 text-left text-xs font-medium text-gray-500 w-16">정렬</th>
          </tr>
        </thead>
      </table>
      <div className="max-h-[40rem] overflow-y-auto">
        <table className="w-full text-sm border-collapse">
          <tbody>
            {details.length === 0 ? (
              <tr>
                <td colSpan={3} className="px-3 py-4 text-center text-xs text-gray-400">
                  데이터가 없습니다.
                </td>
              </tr>
            ) : (
              details.map(dtl => {
                const isSelected = dtl.commonDtlCode === selectedDtl
                return (
                  <tr
                    key={dtl.commonDtlCode}
                    onClick={() => onRowClick(dtl)}
                    className={`h-7 cursor-pointer border-b border-gray-200 ${
                      isSelected
                        ? 'bg-blue-50 text-blue-900'
                        : 'hover:bg-gray-50 text-gray-800'
                    }`}
                  >
                    <td className="px-3 text-xs">{dtl.commonDtlCode}</td>
                    <td className="px-3 text-xs">{dtl.commonDtlCodeNm}</td>
                    <td className="px-3 text-xs text-center">{dtl.sortOrder}</td>
                  </tr>
                )
              })
            )}
          </tbody>
        </table>
      </div>
    </div>
  )
}
