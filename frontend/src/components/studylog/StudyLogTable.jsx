import StudyLogItem from './StudyLogItem'

export default function StudyLogTable({ logs, onDelete, onEditClick }) {
  if ((logs?.length ?? 0) === 0) {
    return (
      <div className="bg-blue-50 border border-blue-200 rounded-xl p-6 text-center text-sm text-blue-700">
        아직 저장된 학습 기록이 없습니다. 위 폼으로 첫 번째 학습 내용을 추가해보세요!
      </div>
    )
  }

  return (
    <div className="bg-white rounded-xl shadow-sm border border-gray-100 overflow-hidden">
      <div className="p-6 pb-4">
        <h2 className="text-lg font-semibold text-gray-800">학습 기록 목록</h2>
      </div>
      <table className="w-full text-sm">
        <thead className="bg-blue-50 text-blue-800 text-left">
          <tr>
            <th className="px-6 py-3 font-medium">#</th>
            <th className="px-6 py-3 font-medium">학습 내용</th>
            <th className="px-6 py-3 font-medium">날짜</th>
            <th className="px-6 py-3 font-medium">관리</th>
          </tr>
        </thead>
        <tbody className="divide-y divide-gray-100">
          {logs?.map(log => (
            <StudyLogItem
              key={log?.id}
              log={log}
              onEditClick={onEditClick}
              onDelete={onDelete}
            />
          ))}
        </tbody>
      </table>
    </div>
  )
}
