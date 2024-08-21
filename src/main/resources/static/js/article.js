//delete
const deleteButton = document.getElementById('delete-btn');

if (deleteButton){
    deleteButton.addEventListener('click', ev => {
        let id = document.getElementById('article-id').value;

        fetch(`/api/articles/${id}`, {
            method : 'DELETE'
        })
            .then(() => {
                alert('삭제가 완료되었습니다.');
                location.replace('/articles');
            });
    });
}

//modify : 쿼리 파라미터에서 id 값을 가져오고, 해당 title과 content를 입력값으로 바꾼 JSON 데이터를 PUT 요청
const modifyButton = document.getElementById('modify-btn');

if(modifyButton){
    modifyButton.addEventListener('click', ev => {
        let params = new URLSearchParams(location.search);
        let id = params.get('id');  //쿼리 파라미터에서 id 값을 가져옴

        console.log("id >> " + id);

        fetch(`/api/articles/${id}`,{
            method: 'PUT',
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify({
                title: document.getElementById('title').value,
                content: document.getElementById('content').value
            })
        })
            .then(() => {
                alert('수정이 완료되었습니다.');
                location.replace(`/articles/${id}`);
            });
    });
}

//create
const createButton = document.getElementById('create-btn');

if(createButton){
    createButton.addEventListener('click', ev => {
        fetch("/api/articles", {
            method: 'POST',
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                title: document.getElementById('title').value,
                content: document.getElementById('content').value
            })
        })
            .then(() => {
                alert('등록이 완료되었습니다.');
                location.replace('/articles');
            });
    });
}