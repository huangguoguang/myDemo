define(['module/message/model', 'module/message/view','text!module/common/footer.html'], function (Model, View, Footer) {
    var controller = function() {
        var model = new Model();
        var view = new View({model:model});
        view.render();
        model.fetch(1);

        controller.onRouteChange = function () {
            view.undelegateEvents();
        };
    };
	controller.footer = Footer;
	controller.className = 'me';
    return controller;
});