define(['module/address/model', 'module/address/view','text!module/common/footer.html'], function (Model, View, Footer) {
    var controller = function() {
        var model = new Model();
        var view = new View({model:model});
        view.render();
        model.fetch(1);

        controller.onRouteChange = function () {
            view.undelegateEvents();
        };
    };
	controller.footer = '<div class="add_address_btn">添加新地址</div>';
	controller.className = 'me';
    return controller;
});