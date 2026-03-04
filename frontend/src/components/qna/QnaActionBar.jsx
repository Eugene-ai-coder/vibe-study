import { useNavigate } from 'react-router-dom'
import FloatingActionBar from '../common/FloatingActionBar'

export default function QnaActionBar({ isNew, isOwner, onSave, onDelete }) {
  const navigate = useNavigate()

  return (
    <FloatingActionBar>
      <button
        onClick={() => navigate('/qna')}
        className="h-8 px-4 border border-gray-300 text-sm rounded text-gray-600 hover:bg-gray-50 transition-colors"
      >
        목록
      </button>
      {isOwner && !isNew && (
        <button
          onClick={onDelete}
          className="h-8 px-4 text-red-500 hover:text-red-700 hover:bg-red-50 rounded text-sm transition-colors"
        >
          삭제
        </button>
      )}
      {(isNew || isOwner) && (
        <button
          onClick={onSave}
          className="h-8 px-6 bg-blue-600 hover:bg-blue-700 text-white text-sm rounded transition-colors"
        >
          {isNew ? '저장' : '수정'}
        </button>
      )}
    </FloatingActionBar>
  )
}
