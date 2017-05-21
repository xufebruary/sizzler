function editorBorder() {
	return {
		restrict: 'AE',
		transclude: false,
		replace: true,
		template: '<div class="editor-border">'
		+ '<div class="editor-border-t"></div>'
		+ '<div class="editor-border-r"></div>'
		+ '<div class="editor-border-b"></div>'
		+ '<div class="editor-border-l"></div>'
		+ '</div>',
		link: link
	};

	function link(scope, elem, attrs) {
	}
}

export default editorBorder;
