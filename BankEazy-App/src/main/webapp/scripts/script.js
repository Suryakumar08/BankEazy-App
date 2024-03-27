function toggleDropDownContent() {
    let elementsToToggle = document.getElementsByClassName("dropdown-content");
    let arrowDown = document.getElementById("down-btn");
    let arrowUp = document.getElementById("up-btn");
    for (var i = 0; i < elementsToToggle.length; i++) {
        let element = elementsToToggle[i];
        if (element.style.display === "none" || element.style.display === "") {
            element.style.display = "block";
            arrowDown.style.display = "none";
            arrowUp.style.display = "block";
        } else {
            element.style.display = "none";
            arrowDown.style.display = "block";
            arrowUp.style.display = "none";
        }
    }
}



function changeBody(id) {

    var idToid = {
        "Withdraw": "withdraw-body",
        "Deposit" : "deposit-body",
        "Intra-bank-transfer" : "intra-bank-transfer-body",
        "Inter-bank-transfer" : "inter-bank-transfer-body"
     }

    var el = document.getElementById(id).parentElement;
    if (!el.classList.contains('selected')) {
        document.getElementById(id).classList.add('selected');
        let currBodyElement = document.getElementById(idToid[id]);
        currBodyElement.style.display = "block";
        if(document.getElementById("up-btn").style.display === "block"){
            toggleDropDownContent();
        }
        document.querySelector("title").textContent = id;
        for(let currId in idToid){
            let contentBodyId = idToid.currId;
            if(currId !== id){
                let currEl = document.getElementById(currId);
                if(currEl.classList.contains('selected')){
                    currEl.classList.remove('selected');
                    document.getElementById(idToid[currId]).style.display = 'none';
                }
            }
        }
    }

}


function toggleBalanceView(index){
	let balanceEl = document.getElementsByClassName("balance" + index)[0];
	let viewBalanceEl = document.getElementsByClassName("view-balance" + index)[0];
	if(balanceEl.style.display === 'none'){
		balanceEl.style.display = 'block';
		balanceEl.style.marginTop = "25px";
		viewBalanceEl.style.display = 'none';
	}
	else{
		balanceEl.style.display = 'none';
		viewBalanceEl.style.display = 'block';
		viewBalanceEl.style.marginTop = "25px";
	}
}