angular.module("fazRealtime", [])
    .controller("ArticlesController", function($scope) {
        $scope.articles = [];
        var ws = new WebSocket(CONFIG.ws_url);
        var timer;

        var distinct = function(elementArray) {
            var ids = {};
            elementArray.forEach(function(element) {
                ids[element.id] = element;
            });

            var newArr = [];
            for(var id in ids) {
                newArr.push(ids[id]);
            }
            return newArr;
        }

        ws.onmessage = function(e) {
            var elements = JSON.parse(e.data);

            var newElements = distinct($scope.articles.concat(elements));
            newElements.sort(function(a, b) {
                return b.published - a.published;
            });
            $scope.$apply(function() {
                $scope.articles = newElements;
            });
            updateTime();
        }


        var updateTime = function() {
            if(timer != undefined) {
                clearTimeout(timer);
            }
            var now = new Date();
            $scope.$apply(function() {
                $scope.articles.forEach(function(element) {
                    element.minutesLeft = Math.floor((now.getTime() - element.published) / 60000);
                });
            });

            timer = setTimeout(updateTime, 5000);
        }
    })