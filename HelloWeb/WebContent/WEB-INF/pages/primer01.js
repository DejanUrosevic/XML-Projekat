(function (angular) {
	var blogEntriesCtrl = function ($scope, $resource, $http) {
		var Users = $http.get('http://localhost:8080/HelloWeb/staticPage')
		.success(function (data, status, headers) 
		{
			$scope.users = data;
        })
        .error(function(data, status, headers)
       {
        	console.log(data);
        	console.log(status);
        	console.log(headers);
       });

		
	};
	var app = angular.module('app',['ngResource']);
	app.controller('blogEntriesCtrl', blogEntriesCtrl);
}(angular));