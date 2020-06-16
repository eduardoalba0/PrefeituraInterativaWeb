window.onload = function() {
	window.dispatchEvent(new Event('resize'));
};

window.onresize = function() {
	resizeMap();
};

function resizeMap() {
	var element = document.getElementById("form-map:map");
	if (element) {
		var width = window.innerWidth;
		var height = window.innerHeight;
		element.style.width = width + "px";
		element.style.height = height + "px";
	}
}