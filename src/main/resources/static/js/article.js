//delete
const deleteButton = document.getElementById('delete-btn');

if (deleteButton){
    deleteButton.addEventListener('click', ev => {
        let id = document.getElementById('article-id').value;

        function success(){
            alert('삭제가 완료되었습니다.');
            location.replace('/articles');
        }
        function fail(){
            alert('삭제에 실패했습니다.');
            location.replace('/articles');
        }

        httpRequest('DELETE', `/api/articles/${id}`, null, success, fail);
    });
}

//modify : 쿼리 파라미터에서 id 값을 가져오고, 해당 title과 content를 입력값으로 바꾼 JSON 데이터를 PUT 요청
const modifyButton = document.getElementById('modify-btn');

if(modifyButton){
    modifyButton.addEventListener('click', ev => {
        let params = new URLSearchParams(location.search);
        let id = params.get('id');  //쿼리 파라미터에서 id 값을 가져옴

        body = JSON.stringify({
            title: document.getElementById('title').value,
            content: document.getElementById('content').value
        });

        function success(){
            alert('수정이 완료되었습니다.');
            location.replace(`/articles/${id}`);
        }
        function fail(){
            alert('수정에 실패했습니다.');
            location.replace(`/articles/${id}`);
        }

        httpRequest('PUT', `/api/articles/${id}`, body, success, fail);
    });
}

//create
const createButton = document.getElementById('create-btn');

if(createButton){
    createButton.addEventListener('click', ev => {
        body = JSON.stringify({
            title: document.getElementById('title').value,
            content: document.getElementById('content').value
        });
        function success(){
            alert('등록이 완료되었습니다.')
            location.replace('/articles');
        }
        function fail(){
            alert('등록에 실패했습니다.')
            location.replace('/articles');
        }

        httpRequest('POST', '/api/articles', body, success, fail);
    });
}

//쿠키를 가져오는 함수
function getCookie(key){
    var result = null;
    var cookie = document.cookie.split(';');

    cookie.some(function (item){
        item = item.replace(' ', '');   // 공백제거

        var dic = item.split('=');

        if (key === dic[0]){
            result = dic[1];
            return true;
        }
    });

    return result;
}

//HTTP 요청을 보내는 함수
function httpRequest(method, url, body, success, fail){
    fetch(url, {
        method: method,
        headers: {
            //로컬 스토리지에서 액세스 토큰의 값을 가져와서 헤더에 추가
            Authorization : 'Bearer: ' + localStorage.getItem('access_token'),
            'Content-Type': 'application/json',
        },
        body: body
    }).then(response => {
        if (response.status === 200 || response.status ===201){
            return success();
        }

        // 토큰이 만료된 경우, refresh_token을 통해서 재발급 후 재실행
        const refresh_token = getCookie('refresh_token');
        console.log('refresh_token >> ' + refresh_token);
        
        if (response.status === 401 && refresh_token){
            console.log('response.status === 401 && refresh_token 해당. 토큰 재발급 요청');
            console.log('refresh_token >> ' + refresh_token);

            fetch('/api/token', {
                method: 'POST',
                headers: {
                    Authorization: 'Bearer: ' + localStorage.getItem('access_token'),
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    refresh_token: getCookie('refresh_token')
                }),
            }).then(res => {
                if (res.ok){
                    return res.json();
                }
            }).then(result => {
                // 재발급이 성공하면 로컬 스토리지 값을 새로운 액세스 토큰으로 교체
                localStorage.setItem('access_token', result.accessToken);
                httpRequest(method, url, body, success, fail);
            })
                .catch(error => fail());
        } else {
            return fail();
        }
    })
}