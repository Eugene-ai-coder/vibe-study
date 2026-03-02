import { useState } from 'react'
import ConfirmDialog from '../common/ConfirmDialog'

export default function StudyLogItem({ log, onEditClick, onDelete }) {
  const [confirmOpen, setConfirmOpen] = useState(false)

  return (
    <>
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
            onClick={() => setConfirmOpen(true)}
            className="text-gray-600 hover:text-red-600 border border-gray-300 hover:border-red-400 px-2 py-1 rounded-md text-xs transition-colors"
          >
            삭제
          </button>
        </td>
      </tr>

      {confirmOpen && (
        <ConfirmDialog
          message="삭제하시겠습니까? 이 작업은 되돌릴 수 없습니다."
          onConfirm={() => { setConfirmOpen(false); onDelete(log?.id) }}
          onCancel={() => setConfirmOpen(false)}
        />
      )}
    </>
  )
}
