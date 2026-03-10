<template>
  <MainLayout>
    <Toast :message="successMsg" type="success" @close="successMsg = ''" />
    <Toast :message="errorMsg" type="error" @close="errorMsg = ''" />

    <div class="space-y-4">
      <h1 class="text-xl font-bold text-gray-800">{{ isNew ? 'Q&A 등록' : 'Q&A 상세' }}</h1>

      <!-- 게시글 폼 -->
      <div class="bg-white rounded-lg shadow-sm border border-gray-200 p-4 space-y-3">
        <div>
          <label class="block text-xs text-gray-500 mb-1">제목 <span class="text-blue-400">*</span></label>
          <input v-model="form.title" :readonly="!isNew && !isEditing"
            :class="['w-full h-8 border rounded px-3 text-sm focus:outline-none focus:border-blue-400',
              (!isNew && !isEditing) ? 'border-gray-200 bg-gray-50 text-gray-500' : 'border-gray-300']" />
        </div>
        <div class="flex items-center gap-4">
          <label class="flex items-center gap-2 text-sm text-gray-700">
            <input type="checkbox" v-model="form.noticeYn" true-value="Y" false-value="N"
              :disabled="!isNew && !isEditing" />
            공지사항
          </label>
          <template v-if="form.noticeYn === 'Y'">
            <div>
              <label class="block text-xs text-gray-500 mb-1">공지 시작일</label>
              <input type="date" v-model="form.noticeStartDt" :readonly="!isNew && !isEditing"
                class="h-8 px-2 border border-gray-300 rounded text-sm" />
            </div>
            <div>
              <label class="block text-xs text-gray-500 mb-1">공지 종료일</label>
              <input type="date" v-model="form.noticeEndDt" :readonly="!isNew && !isEditing"
                class="h-8 px-2 border border-gray-300 rounded text-sm" />
            </div>
          </template>
        </div>
        <div>
          <label class="block text-xs text-gray-500 mb-1">내용</label>
          <textarea v-model="form.content" rows="8" :readonly="!isNew && !isEditing"
            :class="['w-full border rounded px-3 py-2 text-sm focus:outline-none focus:border-blue-400 resize-none',
              (!isNew && !isEditing) ? 'border-gray-200 bg-gray-50 text-gray-500' : 'border-gray-300']" />
        </div>
      </div>

      <!-- 댓글 영역 (상세보기 시) -->
      <template v-if="!isNew">
        <div class="bg-white rounded-lg shadow-sm border border-gray-200 p-4">
          <h3 class="text-sm font-semibold text-gray-700 mb-3">댓글</h3>
          <div v-if="comments.length === 0" class="text-center text-xs text-gray-400 py-4">등록된 댓글이 없습니다.</div>
          <div v-else class="space-y-2">
            <div v-for="c in comments" :key="c.commentId"
              :class="['border border-gray-100 rounded p-3 bg-white', c.parentCommentId ? 'ml-8' : '']">
              <div class="flex justify-between items-start">
                <p class="text-sm text-gray-800 flex-1">{{ c.content }}</p>
                <button v-if="c.createdBy === auth.user?.userId" @click="handleCommentDelete(c.commentId)"
                  class="ml-2 text-xs text-red-400 hover:text-red-600">삭제</button>
              </div>
              <span class="text-xs text-gray-400">{{ c.createdBy }} · {{ c.createdDt ? c.createdDt.slice(0, 16) : '' }}</span>
            </div>
          </div>
        </div>

        <!-- 댓글 작성 -->
        <div class="bg-white rounded-lg shadow-sm border border-gray-200 p-4">
          <h3 class="text-sm font-semibold text-gray-700 mb-2">댓글 작성</h3>
          <div class="flex gap-2">
            <textarea v-model="commentContent" rows="3" placeholder="댓글을 입력하세요."
              class="flex-1 border border-gray-300 rounded px-3 py-2 text-sm focus:outline-none focus:border-blue-400 resize-none" />
            <button @click="handleCommentSubmit"
              class="h-8 self-end px-4 bg-blue-600 text-white text-sm rounded whitespace-nowrap">등록</button>
          </div>
        </div>
      </template>
    </div>

    <FloatingActionBar>
      <button @click="router.push('/qna')"
        class="h-8 px-4 border border-gray-300 text-sm rounded text-gray-600 hover:bg-gray-50 transition-colors">목록</button>
      <button v-if="isOwner && !isNew" @click="confirmOpen = true"
        class="h-8 px-4 text-red-500 hover:text-red-700 hover:bg-red-50 rounded text-sm transition-colors">삭제</button>
      <button v-if="isNew || isOwner" @click="handleActionClick"
        class="h-8 px-6 bg-blue-600 hover:bg-blue-700 text-white text-sm rounded transition-colors">
        {{ isNew ? '저장' : (isEditing ? '수정' : '편집') }}
      </button>
    </FloatingActionBar>

    <ConfirmDialog
      v-if="confirmOpen"
      message="게시글을 삭제하시겠습니까? 이 작업은 되돌릴 수 없습니다."
      confirm-text="삭제"
      confirm-type="danger"
      @confirm="handleDelete"
      @cancel="confirmOpen = false"
    />

    <ConfirmDialog
      v-if="saveConfirmOpen"
      :message="saveConfirmMessage"
      confirm-text="저장"
      confirm-type="primary"
      @confirm="handleSaveConfirm"
      @cancel="saveConfirmOpen = false"
    />

    <ConfirmDialog
      v-if="commentDeleteConfirmOpen"
      message="댓글을 삭제하시겠습니까?"
      confirm-text="삭제"
      confirm-type="danger"
      @confirm="handleCommentDeleteConfirm"
      @cancel="commentDeleteConfirmOpen = false"
    />
  </MainLayout>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import { qnaApi } from '../api/qnaApi'
import MainLayout from '../components/common/MainLayout.vue'
import Toast from '../components/common/Toast.vue'
import FloatingActionBar from '../components/common/FloatingActionBar.vue'
import ConfirmDialog from '../components/common/ConfirmDialog.vue'

const route = useRoute()
const router = useRouter()
const auth = useAuthStore()

const isNew = computed(() => !route.params.id)
const form = reactive({ title: '', content: '', createdBy: '', noticeYn: 'N', noticeStartDt: null, noticeEndDt: null })
const comments = ref([])
const commentContent = ref('')
const isEditing = ref(false)
const confirmOpen = ref(false)
const saveConfirmOpen = ref(false)
const saveConfirmMessage = ref('')
const commentDeleteConfirmOpen = ref(false)
const commentDeleteTargetId = ref(null)
const errorMsg = ref('')
const successMsg = ref('')

const isOwner = computed(() => isNew.value || form.createdBy === auth.user?.userId)

onMounted(async () => {
  if (!isNew.value) {
    try {
      const data = await qnaApi.getOne(route.params.id)
      Object.assign(form, data)
    } catch { errorMsg.value = '게시글을 불러오지 못했습니다.' }
    try { comments.value = await qnaApi.getComments(route.params.id) } catch {}
  }
})

const handleActionClick = () => {
  if (isNew.value || isEditing.value) {
    handleSave()
  } else {
    isEditing.value = true
  }
}

const handleSave = () => {
  errorMsg.value = ''
  if (!form.title.trim()) { errorMsg.value = '제목을 입력해 주세요.'; return }
  if (!form.content.trim()) { errorMsg.value = '내용을 입력해 주세요.'; return }
  saveConfirmMessage.value = isNew.value ? '게시글을 등록하시겠습니까?' : '게시글을 수정하시겠습니까?'
  saveConfirmOpen.value = true
}

const handleSaveConfirm = async () => {
  saveConfirmOpen.value = false
  try {
    if (isNew.value) {
      await qnaApi.create({ ...form, createdBy: auth.user?.userId ?? 'SYSTEM' })
      successMsg.value = '게시글이 등록되었습니다.'
      router.push('/qna')
    } else {
      const updated = await qnaApi.update(route.params.id, { ...form, createdBy: auth.user?.userId ?? 'SYSTEM' })
      Object.assign(form, updated)
      isEditing.value = false
      successMsg.value = '게시글이 수정되었습니다.'
    }
  } catch (err) {
    errorMsg.value = err?.response?.data?.message || '저장에 실패했습니다.'
  }
}

const handleDelete = async () => {
  confirmOpen.value = false
  try {
    await qnaApi.delete(route.params.id)
    successMsg.value = '게시글이 삭제되었습니다.'
    setTimeout(() => router.push('/qna'), 500)
  } catch (err) {
    errorMsg.value = err?.response?.status === 403 ? '삭제 권한이 없습니다.' : '삭제에 실패했습니다.'
  }
}

const handleCommentSubmit = async () => {
  if (!commentContent.value.trim()) return
  try {
    await qnaApi.createComment(route.params.id, { content: commentContent.value.trim(), createdBy: auth.user?.userId ?? 'SYSTEM' })
    comments.value = await qnaApi.getComments(route.params.id)
    commentContent.value = ''
    successMsg.value = '댓글이 등록되었습니다.'
  } catch { errorMsg.value = '댓글 등록에 실패했습니다.' }
}

const handleCommentDelete = (commentId) => {
  commentDeleteTargetId.value = commentId
  commentDeleteConfirmOpen.value = true
}

const handleCommentDeleteConfirm = async () => {
  commentDeleteConfirmOpen.value = false
  try {
    await qnaApi.deleteComment(route.params.id, commentDeleteTargetId.value)
    comments.value = await qnaApi.getComments(route.params.id)
    successMsg.value = '댓글이 삭제되었습니다.'
  } catch { errorMsg.value = '댓글 삭제에 실패했습니다.' }
}
</script>
