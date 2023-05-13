function getId(id){
	return document.getElementById(id);
}

var data = {};//전송 데이터(JSON)

var ws ;
var mid = getId('mid');
var type = getId('type');
var roomId = getId('roomId');
var sender = getId('sender');
var btnLogin = getId('btnLogin');
var btnSend = getId('btnSend');
var talk = getId('talk');
var msg = getId('msg');

btnLogin.onclick = function(){
    //여기도 고쳐야지, 엔드포인트가 변했으니까 그에 맞춰야 함
	ws = new WebSocket("ws://" + location.host + "/ws/chat");

	ws.onmessage = function(msg){ // 함수 안의 함수, 얘는 따로 작동할 수도 있다는 것.
		var data = JSON.parse(msg.data);
		var css;

		if(data.sender == sender.value){
			css = 'class=me';
		}else{
			css = 'class=other';
		}

		var item = `<div ${css} >
		                <span><b>${data.sender}</b></span> [ ${data.time} ]<br/>
                      <span>${data.message}</span>
						</div>`;

		talk.innerHTML += item;
		talk.scrollTop=talk.scrollHeight;//스크롤바 하단으로 이동
	}
}

msg.onkeyup = function(ev){
	if(ev.keyCode == 13){
		send();
	}
}

btnSend.onclick = function(){
	send();
}

function send(){
//	if(msg.value.trim() != ''){
	     // data.mid 라는 걸 새로 지정해준 거, 이런식으로 선언할 수 있음
        data.type = type.value;
        data.roomId = roomId.value;
        data.sender = sender.value;
		data.message = msg.value;
		data.time = new Date().toLocaleString();
		var temp = JSON.stringify(data);
		ws.send(temp);
//	}
	msg.value ='';
}

//function getId(id){
//	return document.getElementById(id);
//}
//
//var data = {};//전송 데이터(JSON)
//
//var ws ;
//var sender = getId('sender');
//var btnLogin = getId('btnLogin');
//var btnSend = getId('btnSend');
//var talk = getId('talk');
//var msg = getId('msg');
//
//btnLogin.onclick = function(){
//    //여기도 고쳐야지, 엔드포인트가 변했으니까 그에 맞춰야 함
//	ws = new WebSocket("ws://" + location.host + "/ws/chat");
//
//	ws.onmessage = function(msg){ // 함수 안의 함수, 얘는 따로 작동할 수도 있다는 것.
//		var data = JSON.parse(msg.data);
//		var css;
//
//		if(data.sender == sender.value){
//			css = 'class=me';
//		}else{
//			css = 'class=other';
//		}
//
//		var item = `<div ${css} >
//		                <span><b>${data.sender}</b></span> [ ${data.time} ]<br/>
//                      <span>${data.message}</span>
//						</div>`;
//
//		talk.innerHTML += item;
//		talk.scrollTop=talk.scrollHeight;//스크롤바 하단으로 이동
//	}
//}
//
//msg.onkeyup = function(ev){
//	if(ev.keyCode == 13){
//		send();
//	}
//}
//
//btnSend.onclick = function(){
//	send();
//}
//
//function send(){
//	if(msg.value.trim() != ''){
//		data.type = getId('type').value; // data.mid 라는 걸 새로 지정해준 거, 이런식으로 선언할 수 있음
//		data.roomId = getId('roomId').value;
//		data.sender = getId('sender').value;
//		data.msg = msg.value;
//		data.time = new Date().toLocaleString();
//		var temp = JSON.stringify(data);
//		ws.send(temp);
//	}
//	msg.value ='';
//}
