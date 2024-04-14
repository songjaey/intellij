$(document).ready(function() {
    // 페이지 로드 시 서버로부터 모든 국가와 지역 정보를 요청하여 블록 생성
    $.ajax({
        type: "GET",
        url: "/admin/getAllLocals",
        success: function(data) {
            data.forEach(function(local) {
                var newLocalBlock = '<div class="local_block">' +
                    '<p class="country">' + local.country + '</p>' +
                    '<p class="local">(' + local.local + ')</p>' +
                    '</div>';

                // Append new local block to content box
                var $newLocalElement = $(newLocalBlock);
                $('.content_box').append($newLocalElement);

                // Handle click event for the newly added local block
                $newLocalElement.on('click', function() {
                    // Get country and local information from clicked block
                    var country = $(this).find('.country').text().trim();
                    var local = $(this).find('.local').text().trim().slice(1, -1); // Remove parentheses

                    // Redirect to localDetail page with parameters
                    window.location.href = '/admin/localDetail?country=' + encodeURIComponent(country) + '&local=' + encodeURIComponent(local);
                });
            });
        },
        error: function(xhr, status, error) {
            console.error("Failed to fetch data:", error);
        }
    });

    // 모달 폼에서 데이터를 추가하고 저장할 경우
    $(document).on('click', '#myModal .btn-secondary', function() {
        var country = $('#countryInput').val();
        var local = $('#localInput').val();

        // 서버에 데이터를 전송하여 저장
        $.ajax({
            type: 'POST',
            url: '/admin/saveLocal',
            data: {
                country: country,
                local: local
            },
            beforeSend: function(xhr) {
                // CSRF 토큰을 요청 헤더에 포함
                xhr.setRequestHeader('X-XSRF-TOKEN', getCsrfToken());
            },
            success: function(response) {
                if (response === "alreadyExists") {
                    alert('이미 존재하는 지역입니다.');
                } else {
                    alert('저장되었습니다!');

                    // 저장 후, 해당 블록을 다시 불러와서 추가
                    var newLocalBlock = '<div class="local_block">' +
                        '<p class="country">' + country + '</p>' +
                        '<p class="local">(' + local + ')</p>' +
                        '</div>';

                    // Append new local block to content box
                    var $newLocalElement = $(newLocalBlock);
                    $('.content_box').append($newLocalElement);

                    // Handle click event for the newly added local block
                    $newLocalElement.on('click', function() {
                        // Redirect to localDetail page with parameters
                        window.location.href = '/admin/localDetail?country=' + encodeURIComponent(country) + '&local=' + encodeURIComponent(local);
                    });

                    // 입력 필드 초기화
                    $('#countryInput').val('');
                    $('#localInput').val('');
                }
            },
            error: function(xhr, status, error) {
                alert('오류가 발생했습니다: ' + error);
                console.error(xhr);
            }
        });
    });
});