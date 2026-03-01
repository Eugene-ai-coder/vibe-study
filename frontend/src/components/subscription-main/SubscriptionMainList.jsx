export default function SubscriptionMainList({ items, selectedId, onRowClick }) {
  return (
    <div className="bg-white rounded-lg shadow-sm border border-gray-200 overflow-hidden">
      <table className="w-full text-sm">
        <thead className="bg-gray-50 border-b border-gray-200">
          <tr>
            <th className="px-3 py-1.5 text-left text-xs font-medium text-gray-500">가입ID</th>
            <th className="px-3 py-1.5 text-left text-xs font-medium text-gray-500">서비스</th>
            <th className="px-3 py-1.5 text-left text-xs font-medium text-gray-500">상품</th>
            <th className="px-3 py-1.5 text-left text-xs font-medium text-gray-500 w-20">대표가입여부</th>
            <th className="px-3 py-1.5 text-left text-xs font-medium text-gray-500">대표가입ID</th>
          </tr>
        </thead>
        <tbody>
          {items.length === 0 ? (
            <tr>
              <td colSpan={5} className="px-3 py-4 text-center text-xs text-gray-400">
                조회 결과가 없습니다.
              </td>
            </tr>
          ) : (
            items.map((item) => {
              const isSelected = item.subsId === selectedId
              return (
                <tr
                  key={item.subsId}
                  onClick={() => onRowClick(item)}
                  className={`h-7 cursor-pointer border-b border-gray-100 ${
                    isSelected
                      ? 'bg-blue-50 text-blue-900'
                      : 'hover:bg-gray-50 text-gray-800'
                  }`}
                >
                  <td className="px-3 text-xs">{item.subsId}</td>
                  <td className="px-3 text-xs">{item.svcNm}</td>
                  <td className="px-3 text-xs">{item.feeProdNm}</td>
                  <td className="px-3 text-xs text-center">{item.mainSubsYn}</td>
                  <td className="px-3 text-xs">{item.mainSubsId || '-'}</td>
                </tr>
              )
            })
          )}
        </tbody>
      </table>
    </div>
  )
}
