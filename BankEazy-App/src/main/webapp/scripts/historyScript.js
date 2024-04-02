function changePage(element){
	let newPage = element.innerText;
	
	let form = document.getElementById('form');
	
	let inputPageElement = document.createElement('input');
	inputPageElement.setAttribute('type','number');
	inputPageElement.setAttribute('name', 'page');
	inputPageElement.setAttribute('value',newPage);
	inputPageElement.setAttribute('hidden','');
	form.appendChild(inputPageElement);
	form.submit();
}

function changePrevPage(currPage){
	let dummyButton = document.createElement('button');
	dummyButton.innerText = -1 + currPage;
	changePage(dummyButton);
}

function changeNextPage(currPage){
	let dummyButton = document.createElement('button');
	dummyButton.innerText = 1 + currPage;
	changePage(dummyButton);
}

const focussed = document.querySelector('.active');
if(focussed != null){
	focussed.scrollIntoView({behavior : "instant"});	
}