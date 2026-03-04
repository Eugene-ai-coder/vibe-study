export default function QnaForm({ data, onChange, readOnly }) {
  return (
    <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-4 space-y-3">
      <div>
        <label className="block text-xs text-gray-500 mb-1">제목</label>
        <input
          name="title"
          value={data.title || ''}
          onChange={onChange}
          readOnly={readOnly}
          className={`w-full h-8 border rounded px-3 text-sm focus:outline-none focus:border-blue-400 ${
            readOnly ? 'border-gray-200 bg-gray-50 text-gray-500' : 'border-gray-300'
          }`}
        />
      </div>
      <div>
        <label className="block text-xs text-gray-500 mb-1">내용</label>
        <textarea
          name="content"
          value={data.content || ''}
          onChange={onChange}
          readOnly={readOnly}
          rows={8}
          className={`w-full border rounded px-3 py-2 text-sm focus:outline-none focus:border-blue-400 resize-none ${
            readOnly ? 'border-gray-200 bg-gray-50 text-gray-500' : 'border-gray-300'
          }`}
        />
      </div>
    </div>
  )
}
