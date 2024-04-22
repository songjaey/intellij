let inputDate1 = null;
let inputDate2 = null;

function createCalendar(year, month, containerId) {
    const calendarBody = document.getElementById(containerId);
    calendarBody.innerHTML = '';

    const monthNames = ['1월', '2월', '3월', '4월', '5월', '6월', '7월', '8월', '9월', '10월', '11월', '12월'];

    const firstDay = new Date(year, month - 1, 1);
    const lastDay = new Date(year, month, 0);
    const daysInMonth = lastDay.getDate();
    const startDay = firstDay.getDay(); // 0 (일요일)부터 6 (토요일)

    let date = 1;
    const table = document.createElement('table');

    // 월 이름과 요일 추가
    const headerRow = document.createElement('tr');
    for (let day = 0; day < 7; day++) {
        const th = document.createElement('th');
        th.textContent = ['일', '월', '화', '수', '목', '금', '토'][day];
        headerRow.appendChild(th);
    }
    table.appendChild(headerRow);

    // 달력 날짜 추가
    for (let i = 0; i < 6; i++) {
        const row = document.createElement('tr');
        for (let j = 0; j < 7; j++) {
            const cell = document.createElement('td');
            if (i === 0 && j < startDay) {
                // 현재 월의 시작 요일 이전은 빈 셀로 처리
                row.appendChild(cell);
            } else if (date <= daysInMonth) {
                // 달력 날짜 추가 부분 수정
                cell.textContent = date;
                cell.dataset.date = formatDate(new Date(year, month - 1, date)); // 날짜를 data-date 속성에 저장
                cell.addEventListener('click', () => handleDateClick(cell, year, month));
                row.appendChild(cell);
                date++;
            } else {
                // 마지막 주의 빈 칸을 채우기 위해 빈 셀 추가
                row.appendChild(cell);
            }
        }
        table.appendChild(row);
    }

    calendarBody.appendChild(table);

    // 월 표시 업데이트
    const monthDisplay = calendarBody.parentElement.querySelector('.monthDisplay');
    monthDisplay.textContent = `${monthNames[month - 1]} ${year}`;
}




function handleDateClick(clickedCell, year, month) {
    const day = parseInt(clickedCell.textContent); // 클릭된 날짜를 숫자로 변환
    const clickedDate = new Date(year, month - 1, day); // 클릭된 날짜의 Date 객체 생성

    if (!inputDate1) {
        // 입력값1이 비어있을 때 클릭된 날짜를 입력값1으로 설정
        inputDate1 = clickedDate;
        document.getElementById('DurationStart').value = formatDate(inputDate1);
        clickedCell.classList.add('selected');
    } else if (!inputDate2) {
        // 입력값2가 비어있을 때 클릭된 날짜를 입력값2로 설정
        if (clickedDate <= inputDate1) {
            // 입력값1보다 이전 날짜를 클릭하면 입력값1 재설정
            resetInputDates(); // 현재 선택을 초기화
            inputDate1 = clickedDate;
            document.getElementById('DurationStart').value = formatDate(inputDate1);
            clickedCell.classList.add('selected');
        } else {
            // 입력값1 이후의 날짜를 클릭하면 입력값2 설정
            inputDate2 = clickedDate;

            const diffInDays = Math.ceil((inputDate2 - inputDate1) / (1000 * 60 * 60 * 24));

            if (diffInDays > 10) {
                // 선택된 날짜 간격이 10일을 초과하는 경우
                alert("여행일정은 10일을 넘길 수 없습니다.");
                resetInputDates(); // 선택 초기화
            } else {
                document.getElementById('DurationEnd').value = formatDate(inputDate2);
                clickedCell.classList.add('selected');
                highlightSelectedRange();
            }
        }
    } else {
        // 이미 입력값1과 입력값2가 모두 설정된 경우
        // 새로운 범위를 선택하기 위해 모든 입력값 초기화
        resetInputDates();
        // 클릭된 날짜를 입력값1으로 설정
        inputDate1 = clickedDate;
        document.getElementById('DurationStart').value = formatDate(inputDate1);
        clickedCell.classList.add('selected');
    }
}



function formatDate(date) {
    const monthNames = ['1월', '2월', '3월', '4월', '5월', '6월', '7월', '8월', '9월', '10월', '11월', '12월'];
    const day = date.getDate();
    const monthIndex = date.getMonth(); // 월 인덱스 (0부터 시작)
    const monthName = monthNames[monthIndex];
    return `${monthName} ${day}일`;
}

function highlightSelectedRange() {
    if (!inputDate1 || !inputDate2) return;

    const minDate = new Date(Math.min(inputDate1.getTime(), inputDate2.getTime()));
    const maxDate = new Date(Math.max(inputDate1.getTime(), inputDate2.getTime()));

    const cells = document.querySelectorAll('td');
    cells.forEach(cell => {
        const cellDateString = cell.dataset.date;
        const cellDate = new Date(cellDateString);

        console.log('Cell Date:', cellDate, 'Min Date:', minDate, 'Max Date:', maxDate);

        if (cellDate >= minDate && cellDate <= maxDate) {
            cell.classList.add('highlight');
        } else {
            cell.classList.remove('highlight');
        }
    });
}

function resetInputDates() {
    inputDate1 = null;
    inputDate2 = null;
    document.getElementById('DurationStart').value = '';
    document.getElementById('DurationEnd').value = '';

    const cells = document.querySelectorAll('td');
    cells.forEach(cell => {
        cell.classList.remove('selected');
        cell.classList.remove('highlight');
    });
}

// 현재 연도와 월을 가져와서 현재 달력과 다음 달력 생성
function createCurrentAndNextMonthCalendar() {
    const currentDate = new Date();
    const currentYear = currentDate.getFullYear();
    const currentMonth = currentDate.getMonth() + 1; // 현재 월 (1부터 시작)
    const nextMonth = currentMonth % 12 + 1; // 다음 달 (1부터 시작)

    // 현재 달력 생성
    createCalendar(currentYear, currentMonth, 'calendarContent');

    // 다음 달력 생성
    createCalendar(currentYear + (nextMonth === 1 ? 1 : 0), nextMonth, 'nextMonthCalendar');
}

// 페이지 로드 시 달력 생성 함수 실행
window.addEventListener('load', createCurrentAndNextMonthCalendar);