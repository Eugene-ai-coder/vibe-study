import { useState, useEffect } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import useQna from '../hooks/useQna'
import MainLayout from '../components/common/MainLayout'
import Toast from '../components/common/Toast'
import ConfirmDialog from '../components/common/ConfirmDialog'
import QnaForm from '../components/qna/QnaForm'
import QnaCommentList from '../components/qna/QnaCommentList'
import QnaCommentForm from '../components/qna/QnaCommentForm'
import QnaActionBar from '../components/qna/QnaActionBar'

const EMPTY_FORM = { title: '', content: '' }

export default function QnaDetailPage() {
  const { id }   = useParams()
  const isNew    = id === undefined
  const navigate = useNavigate()
  const { user } = useAuth()
  const { fetchOne, createQna, updateQna, deleteQna, fetchComments, createComment, deleteComment } = useQna()

  const [form, setForm]               = useState(EMPTY_FORM)
  const [comments, setComments]       = useState([])
  const [isEditing, setIsEditing]     = useState(isNew)
  const [confirmOpen, setConfirmOpen] = useState(false)
  const [successMsg, setSuccessMsg]   = useState(null)
  const [errorMsg, setErrorMsg]       = useState(null)

  const isOwner = isNew || (form.createdBy === user?.userId)

  useEffect(() => {
    if (!isNew) {
      fetchOne(id)
        .then(data => setForm(data))
        .catch(() => setErrorMsg('게시글을 불러오지 못했습니다.'))
      fetchComments(id)
        .then(data => setComments(data))
        .catch(() => {})
    }
  }, [id, isNew])

  const handleFormChange = (e) => {
    const { name, value } = e.target
    setForm(prev => ({ ...prev, [name]: value }))
  }

  const handleSave = async () => {
    if (!form.title.trim())   { setErrorMsg('제목을 입력해 주세요.'); return }
    if (!form.content.trim()) { setErrorMsg('내용을 입력해 주세요.'); return }
    try {
      if (isNew) {
        await createQna({ ...form, createdBy: user?.userId ?? 'SYSTEM' })
        setSuccessMsg('게시글이 등록되었습니다.')
        navigate('/qna')
      } else {
        const updated = await updateQna(id, { ...form, createdBy: user?.userId ?? 'SYSTEM' })
        setForm(updated)
        setIsEditing(false)
        setSuccessMsg('게시글이 수정되었습니다.')
      }
    } catch (err) {
      setErrorMsg(err?.response?.data?.message || '저장에 실패했습니다.')
    }
  }

  const handleDelete = async () => {
    try {
      await deleteQna(id)
      navigate('/qna')
    } catch (err) {
      const status = err?.response?.status
      if (status === 403) setErrorMsg('삭제 권한이 없습니다.')
      else setErrorMsg('삭제에 실패했습니다.')
    }
  }

  const handleCommentSubmit = async (content) => {
    try {
      await createComment(id, { content, createdBy: user?.userId ?? 'SYSTEM' })
      const data = await fetchComments(id)
      setComments(data)
    } catch {
      setErrorMsg('댓글 등록에 실패했습니다.')
    }
  }

  const handleCommentDelete = async (commentId) => {
    try {
      await deleteComment(id, commentId)
      const data = await fetchComments(id)
      setComments(data)
    } catch {
      setErrorMsg('댓글 삭제에 실패했습니다.')
    }
  }

  return (
    <MainLayout>
      <Toast message={successMsg} type="success" onClose={() => setSuccessMsg(null)} />
      <Toast message={errorMsg}   type="error"   onClose={() => setErrorMsg(null)} />

      <div className="space-y-4">
        <h1 className="text-xl font-bold text-gray-800">
          {isNew ? 'Q&A 등록' : 'Q&A 상세'}
        </h1>

        <QnaForm
          data={form}
          onChange={handleFormChange}
          readOnly={!isNew && !isEditing}
        />

        {!isNew && (
          <>
            <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-4">
              <h3 className="text-sm font-semibold text-gray-700 mb-3">댓글</h3>
              <QnaCommentList
                comments={comments}
                user={user}
                onDeleteComment={handleCommentDelete}
              />
            </div>
            <QnaCommentForm onSubmit={handleCommentSubmit} />
          </>
        )}
      </div>

      <QnaActionBar
        isNew={isNew}
        isOwner={isOwner}
        onSave={isEditing || isNew ? handleSave : () => setIsEditing(true)}
        onDelete={() => setConfirmOpen(true)}
      />

      {confirmOpen && (
        <ConfirmDialog
          message="게시글을 삭제하시겠습니까? 이 작업은 되돌릴 수 없습니다."
          onConfirm={() => { setConfirmOpen(false); handleDelete() }}
          onCancel={() => setConfirmOpen(false)}
        />
      )}
    </MainLayout>
  )
}
