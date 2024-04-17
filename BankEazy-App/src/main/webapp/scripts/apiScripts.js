function addReadApi() {
	let form = document.getElementById("addApiForm");
	form.action = "addReadApi";
	form.submit();
}
function addWriteApi() {
	let form = document.getElementById("addApiForm");
	form.action = "addWriteApi";
	form.submit();
}

function removeKey(key){
	let form = document.getElementById("addApiForm");
	
	let input = document.createElement("input");
	input.setAttribute('type','text');
	input.setAttribute('name','apiKey');
	input.setAttribute('value',key);
	input.style.display = 'none';
	
	form.appendChild(input);
	form.action = "removeApiKey";
	form.submit();
}
