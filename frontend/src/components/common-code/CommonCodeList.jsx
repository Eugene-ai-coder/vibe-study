export default function CommonCodeList({ codes, selectedCode, onRowClick }) {
  return (
    <div className="bg-white rounded-lg shadow-sm border border-gray-200 overflow-hidden">
      <table className="w-full text-sm border-collapse">
        <thead className="bg-gray-50 border-t border-gray-300 border-b border-gray-300">
          <tr>
            <th className="px-3 py-1.5 text-left text-xs font-medium text-gray-500">공통코드</th>
            <th className="px-3 py-1.5 text-left text-xs font-medium text-gray-500">공통코드명</th>
          </tr>
        </thead>
      </table>
      <div className="max-h-[40rem] overflow-y-auto">
        <table className="w-full text-sm border-collapse">
          <tbody>
            {codes.length === 0 ? (
              <tr>
                <td colSpan={2} className="px-3 py-4 text-center text-xs text-gray-400">
                  데이터가 없습니다.
                </td>
              </tr>
            ) : (
              codes.map(code => {
                const isSelected = code.commonCode === selectedCode
                return (
                  <tr
                    key={code.commonCode}
                    onClick={() => onRowClick(code)}
                    className={`h-7 cursor-pointer border-b border-gray-200 ${
                      isSelected
                        ? 'bg-blue-50 text-blue-900'
                        : 'hover:bg-gray-50 text-gray-800'
                    }`}
                  >
                    <td className="px-3 text-xs">{code.commonCode}</td>
                    <td className="px-3 text-xs">{code.commonCodeNm}</td>
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
