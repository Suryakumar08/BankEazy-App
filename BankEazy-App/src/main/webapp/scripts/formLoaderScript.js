
window.onload = function() {
	handleFormView();
}

function handleFormView() {
	let typeSelectElement = document.getElementById("select-type");
	let selectedValue = typeSelectElement.value;
	if (selectedValue != null) {
		switch (selectedValue) {
			case "Customer": {
				makeVisible("customer-tags");
				makeHidden("employee-tags");
				break;
			}
			case "Admin":
			case "Employee": {
				makeVisible("employee-tags");
				makeHidden("customer-tags");
				break;
			}
		}
	}
}

function makeVisible(className) {
	let elements = document.getElementsByClassName(className);
	for (let i = 0; i < elements.length; i++) {
		let currElement = elements[i];
		currElement.style.display = "flex";
	}
}

function makeHidden(className) {
	let elements = document.getElementsByClassName(className);
	for (let i = 0; i < elements.length; i++) {
		let currElement = elements[i];
		currElement.style.display = "none";
	}
}

function addUserFormSubmitter() {
	let formEl = document.getElementById('dataForm');
	let addingUsertype = document.getElementById('select-type').value;
	console.log(addingUsertype);
	if (addingUsertype === 'Customer') {
		formEl.action = 'addCustomer';
	}
	else {
		formEl.action = 'addEmployee';
	}
	formEl.submit();
}