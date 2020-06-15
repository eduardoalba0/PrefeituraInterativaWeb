function resizeMap() {
	var width = window.innerWidth;
	var height = window.innerHeight;
	var element = document.getElementById("form-map:map");
	element.style.width = width + "px";
	element.style.height = height + "px";
}

window.onload = function () {
	window.dispatchEvent(new Event('resize'));
};

window.onresize = function () {
	resizeMap();
};	