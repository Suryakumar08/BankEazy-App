function toggleDropDownContent() {
	let elementsToToggle = document.getElementsByClassName("dropdown-content");
	let arrowDown = document.getElementById("down-btn");
	let arrowUp = document.getElementById("up-btn");
	for (var i = 0; i < elementsToToggle.length; i++) {
		let element = elementsToToggle[i];
		if (element.style.display === "none" || element.style.display === "") {
			element.style.display = "flex";
			arrowDown.style.display = "none";
			arrowUp.style.display = "block";
		} else {
			element.style.display = "none";
			arrowDown.style.display = "block";
			arrowUp.style.display = "none";
		}
	}
}



function changeTitle(title) {
	document.querySelector("title").textContent = title;
}


function toggleBalanceView(index) {
	let balanceEl = document.getElementsByClassName("balance" + index)[0];
	let viewBalanceEl = document.getElementsByClassName("view-balance" + index)[0];
	if (balanceEl.style.display === 'none') {
		balanceEl.style.display = 'block';
		balanceEl.style.marginTop = "25px";
		viewBalanceEl.style.display = 'none';
	}
	else {
		balanceEl.style.display = 'none';
		viewBalanceEl.style.display = 'block';
		viewBalanceEl.style.marginTop = "25px";
	}
}