import { useState } from 'react'

export default function QnaCommentForm({ onSubmit }) {
  const [content, setContent] = useState('')

  const handleSubmit = async () => {
    if (!content.trim()) return
    await onSubmit(content.trim())
    setContent('')
  }

  return (
    <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-4">
      <h3 className="text-sm font-semibold text-gray-700 mb-2">댓글 작성</h3>
      <div className="flex gap-2">
        <textarea
          value={content}
          onChange={e => setContent(e.target.value)}
          rows={3}
          placeholder="댓글을 입력하세요."
          className="flex-1 border border-gray-300 rounded px-3 py-2 text-sm focus:outline-none focus:border-blue-400 resize-none"
        />
        <button
          onClick={handleSubmit}
          className="h-8 self-end px-4 bg-blue-600 text-white text-sm rounded whitespace-nowrap"
        >
          등록
        </button>
      </div>
    </div>
  )
}
