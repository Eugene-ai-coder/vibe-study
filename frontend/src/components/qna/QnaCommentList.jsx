export default function QnaCommentList({ comments, user, onDeleteComment }) {
  if (!comments || comments.length === 0) {
    return (
      <div className="text-center text-xs text-gray-400 py-4">
        등록된 댓글이 없습니다.
      </div>
    )
  }

  return (
    <div className="space-y-2">
      {comments.map(c => (
        <div
          key={c.commentId}
          className={`border border-gray-100 rounded p-3 bg-white ${c.parentCommentId ? 'ml-8' : ''}`}
        >
          <div className="flex justify-between items-start">
            <p className="text-sm text-gray-800 flex-1">{c.content}</p>
            {c.createdBy === user?.userId && (
              <button
                onClick={() => onDeleteComment(c.commentId)}
                className="ml-2 text-xs text-red-400 hover:text-red-600"
              >
                삭제
              </button>
            )}
          </div>
          <span className="text-xs text-gray-400">
            {c.createdBy} · {c.createdDt ? c.createdDt.slice(0, 16) : ''}
          </span>
        </div>
      ))}
    </div>
  )
}
