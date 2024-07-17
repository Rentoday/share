// 요청 인터셉터와 SSE 모듈을 import했다고 가정합니다.
import { axiosInstance } from '../common/interceptors.js';
import { sseModule } from '../common/sseModule.js';

// 전역 변수 선언
let userInfo = null;
window.allNotices = [];
window.latestNoticeTitle = "작성된 공지사항이 없습니다.";

// 초기화 함수
async function initialize() {
    const token = localStorage.getItem('Authorization');
    const authCookie = Cookies.get('Authorization');

    if (!token && !authCookie) {
        // 인증 정보가 없는 경우
        await fetchNotices();
    } else {
        // 인증 정보가 있는 경우
        sseModule.connect(); // SSE 연결
        await Promise.all([fetchUserInfo(), fetchNotices()]);
    }

    // DOM이 로드된 후 UI 업데이트
    document.addEventListener('DOMContentLoaded', updateUI);
}

// 사용자 정보 가져오기
async function fetchUserInfo() {
    try {
        const response = await axiosInstance.get('/api/member/info');
        userInfo = response.data;
    } catch (error) {
        console.error('Failed to get user info:', error);
    }
}

// 공지사항 가져오기 및 업데이트
async function fetchAndUpdateNotices() {
    try {
        const response = await axiosInstance.get('/api/notice/readAll');
        window.allNotices = response.data;
        if (window.allNotices.length > 0) {
            window.latestNoticeTitle = window.allNotices[window.allNotices.length - 1].title;
        }
    } catch (error) {
        console.error('Failed to fetch notices:', error);
        window.allNotices = [];
    }
}

// UI 업데이트 함수
function updateUI() {
    updateAuthSection();
        updateNoticeUI();
}

// 인증 섹션 업데이트
function updateAuthSection() {
    const authSection = document.querySelector('.auth-section');
    if (!userInfo) {
        authSection.innerHTML = '<button class="login-button">로그인</button>';
    } else {
        authSection.innerHTML = `
            <button class="icon-button panmaetotal">판매등록</button>
            <div class="notification-container">
                <button class="icon-button notification">
                    <img src="/image/common/notificationoff.svg" alt="알림" class="notification-icon" id="notificationIcon">
                </button>
                <div class="notification-dropdown" style="display: none;">
                    <div class="notification-list"></div>
                    <div class="view-all-notifications">
                        <button>모든 알림 보기</button>
                    </div>
                </div>
            </div>
            <div class="profile-container">
                <button class="icon-button profile">
                    <img src="${userInfo.profileImage}" alt="프로필" class="profile-image">
                </button>
                <div class="profile-dropdown" style="display: none;">
                    <button class="dropdown-item mypage">마이페이지</button>
                    <button class="dropdown-item logout">로그아웃</button>
                </div>
            </div>
        `;
        updateNotificationIcon();
        setupNotificationButton();
    }
}

// 공지사항 UI 업데이트
function updateNoticeUI() {
    const noticeText = document.getElementById('noticeText');
    if (noticeText) {
        noticeText.textContent = window.latestNoticeTitle;
    }
    updateNoticeModal(window.allNotices);
    setupNoticeListeners();
}


// 알림 버튼 설정
function setupNotificationButton() {
        const notificationButton = document.querySelector('.icon-button.notification');
        const notificationDropdown = document.querySelector('.notification-dropdown');

        notificationButton.addEventListener('click', () => {
            notificationDropdown.style.display = notificationDropdown.style.display === 'none' ? 'flex' : 'none';
            if (notificationDropdown.style.display === 'flex') {
                updateNotificationDropdown();
            }
        });
    }

// 공지사항 모달 업데이트
function updateNoticeModal(notices) {
    const tbody = document.querySelector('#noticeModal .notice-table tbody');
            if (!tbody) return;

            tbody.innerHTML = ''; // 기존 내용 초기화

            if (notices.length === 0) {
                // 공지사항이 없는 경우
                const tr = document.createElement('tr');
                tr.innerHTML = '<td colspan="4" style="text-align: center;">작성된 공지사항이 없습니다.</td>';
                tbody.appendChild(tr);
            } else {
                // 공지사항이 있는 경우, 기존 로직 실행
                notices.forEach((notice, index) => {
                    const tr = document.createElement('tr');
                    tr.innerHTML = `
                        <td>${notices.length - index}</td>
                        <td>${notice.title}</td>
                        <td>${formatDate(notice.createdAt)}</td>
                        <td class="toggle-button" onclick="toggleContent(this)">&#x25BC;</td>
                    `;
                    tbody.appendChild(tr);

                    const contentTr = document.createElement('tr');
                    contentTr.className = 'expandable-content';
                    contentTr.style.display = 'none';
                    contentTr.innerHTML = `
                        <td colspan="4" style="text-align: left;">
                            ${notice.content.replace(/\n/g, '<br>')}
                        </td>
                    `;
                    tbody.appendChild(contentTr);
                });
}

// 공지사항 리스너 설정
function setupNoticeListeners() {
       const noticeText = document.getElementById('noticeText');
       const modal = document.getElementById('noticeModal');
       const closeModal = document.getElementById('closeModal');

       if (noticeText && modal && closeModal) {
           noticeText.addEventListener('click', function() {
               modal.style.display = 'flex';
           });

           closeModal.addEventListener('click', function() {
               modal.style.display = 'none';
           });

           window.addEventListener('click', function(event) {
               if (event.target == modal) {
                   modal.style.display = 'none';
               }
           });
       }
   }

// 여기에 setupNotificationButton, updateNotificationIcon, updateNotificationDropdown 함수를 추가하세요.
// (이 함수들은 이전 코드에서 가져올 수 있습니다)
function updateNotificationIcon() {
        const notificationIcon = document.getElementById('notificationIcon');
        if (notificationIcon) {
            notificationIcon.src = hasUnreadNotifications
                ? "/image/common/notificationon.svg"
                : "/image/common/notificationoff.svg";
        }
    }

    function updateNotificationDropdown() {
        const notificationList = document.querySelector('.notification-list');
        if (notifications.length === 0) {
            notificationList.innerHTML = '<div class="no-notifications">표시할 알림이 없습니다.</div>';
        } else {
            notificationList.innerHTML = notifications.map(notification => `
                <div class="notification-item">
                    ${notification.message}
                </div>
            `).join('');
        }
    }



// 공지사항 모달 업데이트 및 리스너 설정 함수
function updateNoticeModal(notices) {
    // 이전 코드와 동일
}

function setupNoticeListeners() {
    // 이전 코드와 동일
}

function formatDate(dateString) {
        const date = new Date(dateString);
        const year = date.getFullYear().toString().slice(-2);
        const month = (date.getMonth() + 1).toString().padStart(2, '0');
        const day = date.getDate().toString().padStart(2, '0');
        return `${year}/${month}/${day}`;
    }

//공지사항 아코디언 (기존 코드 유지)
    function toggleContent(button) {
        const row = button.closest('tr');
        const nextRow = row.nextElementSibling;
        const isExpanded = nextRow.style.display === 'table-row';
        if (isExpanded) {
            nextRow.style.display = 'none';
            button.innerHTML = '&#x25BC;'; // Down arrow
        } else {
            nextRow.style.display = 'table-row';
            button.innerHTML = '&#x25B2;'; // Up arrow
        }
    }

    // 공지사항 JS
        document.addEventListener('DOMContentLoaded', function() {
            var noticeText = document.getElementById('noticeText');
            var modal = document.getElementById('noticeModal');
            var closeModal = document.getElementById('closeModal');

            // 모달 내용 업데이트
            updateNoticeModal(window.allNotices);

            noticeText.addEventListener('click', function() {
                modal.style.display = 'flex';
            });

            closeModal.addEventListener('click', function() {
                modal.style.display = 'none';
            });

            window.addEventListener('click', function(event) {
                if (event.target == modal) {
                    modal.style.display = 'none';
                }
            });
        });



            // 모달에 스크롤 추가 (공지사항이 있을 때만)
            const noticeTable = document.querySelector('#noticeModal .notice-table');
            if (notices.length > 0) {
                noticeTable.style.maxHeight = '400px';
                noticeTable.style.overflowY = 'auto';
            } else {
                noticeTable.style.maxHeight = 'none';
                noticeTable.style.overflowY = 'visible';
            }
        }

// 초기화 함수 실행
initialize();
