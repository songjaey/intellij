$(document).ready(function() {



   /* // 모달 폼에서 데이터를 추가하고 저장할 경우
    $('#open-modal').click(function() {
        //        event.preventDefault(); // 기본 동작 방지

        var country = $('#countryInput').val();
        var local = $('#localInput').val();
        var csrfToken = getCsrfToken(); // CSRF 토큰 가져오기

        // 서버에 데이터를 전송하여 저장
        $.ajax({
            type: 'POST',
            url: '/admin/saveLocal',
            data: {
                country: country,
                local: local
            },
            headers: {
                'X-XSRF-TOKEN': csrfToken // 헤더에 CSRF 토큰 추가
            },
            success: function (response) {
                if (response === "alreadyExists") {
                    alert('이미 존재하는 지역입니다.');
                } else {
                    alert('저장되었습니다!');

                    // 저장 후, 해당 블록을 다시 불러와서 추가
                    var newLocalBlock = '<div class="local_block">' +
                    '<p class="country">' + local.country + '</p>' +
                    '<p class="local">(' + local.local + ')</p>' +
                    '<button class="delete">삭제</button>' +
                    '</div>' ;


                    // Append new local block to content box
                    var $newLocalElement = $(newLocalBlock);
                    $('.content_box').append($newLocalElement);

                    // 입력 필드 초기화
                    $('#countryInput').val('');
                    $('#localInput').val('');
                }
            },
            error: function (xhr, status, error) {
                // 오류 발생 시 처리
                console.error('오류 발생:', error);
                alert('오류가 발생했습니다: ' + error);
            }
        });

    });*/


});