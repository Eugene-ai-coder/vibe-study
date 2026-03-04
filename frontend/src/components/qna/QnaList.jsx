import { NavLink } from 'react-router-dom'

export default function QnaList({ items, page, totalPages, onPageChange }) {
  return (
    <div className="bg-white rounded-lg shadow-sm border border-gray-200 overflow-hidden">
      <table className="w-full text-sm border-collapse">
        <thead className="bg-gray-50 border-t border-gray-300 border-b border-gray-300">
          <tr>
            <th className="px-3 py-1.5 text-left text-xs font-medium text-gray-500 w-16">번호</th>
            <th className="px-3 py-1.5 text-left text-xs font-medium text-gray-500">제목</th>
            <th className="px-3 py-1.5 text-left text-xs font-medium text-gray-500 w-24">작성자</th>
            <th className="px-3 py-1.5 text-left text-xs font-medium text-gray-500 w-20">답변</th>
            <th className="px-3 py-1.5 text-left text-xs font-medium text-gray-500 w-16">조회</th>
            <th className="px-3 py-1.5 text-left text-xs font-medium text-gray-500 w-28">등록일</th>
          </tr>
        </thead>
      </table>
      <div className="max-h-[40rem] overflow-y-auto">
        <table className="w-full text-sm border-collapse">
          <tbody>
            {items.length === 0 ? (
              <tr>
                <td colSpan={6} className="px-3 py-4 text-center text-xs text-gray-400">
                  데이터가 없습니다.
                </td>
              </tr>
            ) : (
              items.map((item, idx) => (
                <tr key={item.qnaId} className="h-7 border-b border-gray-200 hover:bg-gray-50">
                  <td className="px-3 text-xs text-gray-500">{page * 10 + idx + 1}</td>
                  <td className="px-3 text-xs">
                    <NavLink to={`/qna/${item.qnaId}`} className="text-[#2563EB] hover:underline">
                      {item.title}
                    </NavLink>
                  </td>
                  <td className="px-3 text-xs">{item.createdBy}</td>
                  <td className="px-3 text-xs">
                    <span className={`px-1.5 py-0.5 rounded text-xs ${
                      item.answerYn === 'Y' ? 'bg-green-100 text-green-700' : 'bg-gray-100 text-gray-500'
                    }`}>
                      {item.answerYn === 'Y' ? '답변완료' : '대기'}
                    </span>
                  </td>
                  <td className="px-3 text-xs text-gray-500">{item.viewCnt ?? 0}</td>
                  <td className="px-3 text-xs text-gray-500">{item.createdDt ? item.createdDt.slice(0, 10) : ''}</td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>
      {/* 페이지네이션 */}
      {totalPages > 0 && (
        <div className="flex justify-center items-center gap-2 py-3 border-t border-gray-100">
          <button
            onClick={() => onPageChange(page - 1)}
            disabled={page === 0}
            className="h-7 px-3 border border-gray-300 rounded text-xs text-gray-600 hover:bg-gray-50 disabled:opacity-40"
          >
            이전
          </button>
          <span className="text-xs text-gray-500">{page + 1} / {totalPages}</span>
          <button
            onClick={() => onPageChange(page + 1)}
            disabled={page >= totalPages - 1}
            className="h-7 px-3 border border-gray-300 rounded text-xs text-gray-600 hover:bg-gray-50 disabled:opacity-40"
          >
            다음
          </button>
        </div>
      )}
    </div>
  )
}
