export default function StudyLogItem({ log, onEditClick, onDelete }) {
  const handleDelete = async () => {
    if (!confirm('삭제하시겠습니까?')) return
    await onDelete(log?.id)
  }

  return (
    <tr className="hover:bg-gray-50 transition-colors">
      <td className="px-6 py-3 text-gray-500">{log?.id ?? '-'}</td>
      <td className="px-6 py-3 text-gray-800">{log?.content || '-'}</td>
      <td className="px-6 py-3 text-gray-600">{log?.date || '-'}</td>
      <td className="px-6 py-3 flex gap-2">
        <button
          onClick={() => onEditClick(log)}
          className="text-gray-600 hover:text-blue-600 border border-gray-300 hover:border-blue-400 px-2 py-1 rounded-md text-xs transition-colors"
        >
          수정
        </button>
        <button
          onClick={handleDelete}
          className="text-gray-600 hover:text-red-600 border border-gray-300 hover:border-red-400 px-2 py-1 rounded-md text-xs transition-colors"
        >
          삭제
        </button>
      </td>
    </tr>
  )
}
