var mapContainer = document.getElementById('map'), // 지도를 표시할 div
        mapOption = {
            center: new kakao.maps.LatLng(37.566826, 126.9786567), // 지도의 중심좌표 (서울시청)
            level: 3 // 지도의 확대 레벨
        };

    // 지도를 표시할 div와  지도 옵션으로  지도를 생성합니다
    var map = new kakao.maps.Map(mapContainer, mapOption);

    document.addEventListener('DOMContentLoaded', function() {
            var noticeText = document.getElementById('noticeText');
            var modal = document.getElementById('noticeModal');
            var closeModal = document.getElementById('closeModal');

            noticeText.addEventListener('click', function() {
                modal.style.display = 'flex';  // 'block' 대신 'flex' 사용
            });

            closeModal.addEventListener('click', function() {
                modal.style.display = 'none';
            });

            window.addEventListener('click', function(event) {
                if (event.target == modal) {
                    modal.style.display = 'none';
                }
            });

    // 로그인 모달 관련 코드 추가
    var loginButton = document.querySelector('.header-content .login-button');
    var loginModal = document.getElementById('loginModal');
    var closeLoginModal = document.getElementById('closeLoginModal');

    loginButton.addEventListener('click', function(e) {
        e.preventDefault();
        loginModal.style.display = 'flex';
    });

    closeLoginModal.addEventListener('click', function() {
        loginModal.style.display = 'none';
    });

    window.addEventListener('click', function(event) {
        if (event.target == loginModal) {
            loginModal.style.display = 'none';
        }
    });

     // 시간 드롭다운 관련 코드 추가
    var timeDropdown = document.querySelector('.time-dropdown');
    var timeDisplay = timeDropdown.querySelector('.time-display');
    var timeOptions = timeDropdown.querySelector('.time-options');

    // 시간 옵션 생성 (한 시간 간격)
    function generateTimeOptions() {
        for (let hour = 0; hour < 24; hour++) {
            let timeString = `${hour.toString().padStart(2, '0')}:00`;
            let option = document.createElement('div');
            option.className = 'time-option';
            option.textContent = timeString;
            option.onclick = function() {
                timeDisplay.textContent = this.textContent;
                timeOptions.style.display = 'none';
            };
            timeOptions.appendChild(option);
        }
    }

    generateTimeOptions();

    timeDisplay.addEventListener('click', function(e) {
        e.stopPropagation();
        timeOptions.style.display = timeOptions.style.display === 'none' ? 'block' : 'none';
    });

    document.addEventListener('click', function() {
        timeOptions.style.display = 'none';
    });

    timeOptions.addEventListener('click', function(e) {
        e.stopPropagation();
    });
        });

        //공지사항 아코디언
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