import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import useQna from '../hooks/useQna'
import MainLayout from '../components/common/MainLayout'
import Toast from '../components/common/Toast'
import FloatingActionBar from '../components/common/FloatingActionBar'
import QnaList from '../components/qna/QnaList'

export default function QnaPage() {
  const navigate = useNavigate()
  const { fetchList } = useQna()

  const [items, setItems]           = useState([])
  const [keyword, setKeyword]       = useState('')
  const [page, setPage]             = useState(0)
  const [totalPages, setTotalPages] = useState(0)
  const [errorMsg, setErrorMsg]     = useState(null)

  const handleSearch = async (p = 0) => {
    try {
      const data = await fetchList({ keyword: keyword.trim() || undefined, page: p, size: 10 })
      if (data && data.content !== undefined) {
        setItems(data.content)
        setTotalPages(data.totalPages)
      } else {
        setItems(Array.isArray(data) ? data : [])
        setTotalPages(1)
      }
      setPage(p)
    } catch {
      setErrorMsg('조회에 실패했습니다.')
    }
  }

  const handlePageChange = (newPage) => {
    if (newPage < 0 || newPage >= totalPages) return
    handleSearch(newPage)
  }

  return (
    <MainLayout>
      <Toast message={errorMsg} type="error" onClose={() => setErrorMsg(null)} />

      <div className="space-y-4">
        <h1 className="text-xl font-bold text-gray-800">Q&A 게시판</h1>

        {/* 조회영역 */}
        <div className="bg-gray-50 rounded-lg shadow-sm p-4 flex items-end gap-4">
          <div>
            <label className="block text-xs text-gray-500 mb-1">검색어</label>
            <input
              value={keyword}
              onChange={e => setKeyword(e.target.value)}
              onKeyDown={e => e.key === 'Enter' && handleSearch(0)}
              placeholder="제목 또는 내용"
              className="h-8 px-3 border border-gray-300 rounded text-sm focus:outline-none focus:border-blue-400 w-64"
            />
          </div>
          <button
            onClick={() => handleSearch(0)}
            className="h-8 px-4 bg-blue-600 text-white text-sm rounded hover:bg-blue-700"
          >
            검색
          </button>
        </div>

        {/* 목록 */}
        <QnaList
          items={items}
          page={page}
          totalPages={totalPages}
          onPageChange={handlePageChange}
        />
      </div>

      {/* 하단 액션바 */}
      <FloatingActionBar>
        <button
          onClick={() => navigate('/qna/new')}
          className="h-8 px-6 bg-blue-600 text-white text-sm rounded hover:bg-blue-700"
        >
          등록
        </button>
      </FloatingActionBar>
    </MainLayout>
  )
}
