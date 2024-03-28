$(document).ready(function() {
    $('.search').click(function() {
        var selectedAccount = $('#accounts-select').val();
        var fromDate = $('#from-date').val();
        var toDate = $('#to-date').val();
        var pageNumber = 1;
        var requestData = {
            account: selectedAccount,
            from_date: fromDate,
            to_date: toDate,
            page: pageNumber
        };
		console.log(requestData);
        $.ajax({
            type: 'POST',
            url: '/BankEazy-App/user/transactionHistory',
            data: requestData
        });
    });
    $('.pageNumber').click(function(event){
		event.preventDefault();
		var selectedAccount = $('#accounts-select').val();
		var fromDate = $('#from-date').val();
		var toDate = $('#to-date').val();
		var pageNumber = $(this).text();
		
		var requestData = {
			account: selectedAccount,
            from_date: fromDate,
            to_date: toDate,
            page: pageNumber
		};
		
		$.ajax({
			type:'POST',
			url:'/BankEazy-App/user/transactionHistory',
			data:requestData
		});
	});
	$('.prev').click(function(event){
		event.preventDefault();
		var selectedAccount = $('#accounts-select').val();
		var fromDate = $('#from-date').val();
		var toDate = $('#to-date').val();
		var pageNumber = parseInt($(this).text()) - 1;
		
		var requestData = {
			account: selectedAccount,
            from_date: fromDate,
            to_date: toDate,
            page: pageNumber
		};
		
		$.ajax({
			type:'POST',
			url:'/BankEazy-App/user/transactionHistory',
			data:requestData
		});
	});
	$('.next').click(function(event){
		event.preventDefault();
		var selectedAccount = $('#accounts-select').val();
		var fromDate = $('#from-date').val();
		var toDate = $('#to-date').val();
		var pageNumber = parseInt($(this).text()) + 1;
		
		var requestData = {
			account: selectedAccount,
            from_date: fromDate,
            to_date: toDate,
            page: pageNumber
		};
		
		$.ajax({
			type:'POST',
			url:'/BankEazy-App/user/transactionHistory',
			data:requestData
		});
	});
});
