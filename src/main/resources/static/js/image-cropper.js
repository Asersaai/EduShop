/**
 * Generic in-place image cropper for <input type="file"> fields.
 *
 * Usage: add to any file input:
 *   data-cropper                          -> enables cropping for this input
 *   data-aspect-ratio="1"                 -> optional, e.g. "1" for square avatars,
 *                                            "1.3333" for 4:3 product photos. Omit for free-form crop.
 *   data-preview="#someImgElementSelector" -> optional <img> to update with the cropped result
 *
 * Requires Cropper.js (https://github.com/fengyuanchen/cropperjs) to be loaded on the page.
 */
document.addEventListener('DOMContentLoaded', function () {
    document.querySelectorAll('input[type="file"][data-cropper]').forEach(setupCropperInput);
});

function setupCropperInput(input) {
    var aspectRatioAttr = input.dataset.aspectRatio;
    var aspectRatio = aspectRatioAttr ? parseFloat(aspectRatioAttr) : NaN;
    var previewSelector = input.dataset.preview;
    var preview = previewSelector ? document.querySelector(previewSelector) : null;

    var modal = document.createElement('div');
    modal.className = 'cropper-modal-overlay';
    modal.innerHTML =
        '<div class="cropper-modal">' +
        '  <div class="cropper-modal-head">' +
        '    <h4>Обрежьте изображение</h4>' +
        '    <button type="button" class="cropper-close" aria-label="Закрыть">&times;</button>' +
        '  </div>' +
        '  <div class="cropper-modal-body">' +
        '    <img class="cropper-image" alt="" />' +
        '  </div>' +
        '  <div class="cropper-modal-actions">' +
        '    <button type="button" class="btn btn-ghost cropper-cancel">Отмена</button>' +
        '    <button type="button" class="btn btn-primary cropper-apply">Применить</button>' +
        '  </div>' +
        '</div>';
    document.body.appendChild(modal);

    var imgEl = modal.querySelector('.cropper-image');
    var cropper = null;
    var originalFileName = 'image.jpg';
    var originalFileType = 'image/jpeg';

    input.addEventListener('change', function (e) {
        var file = e.target.files && e.target.files[0];
        if (!file) return;

        originalFileName = file.name || 'image.jpg';
        originalFileType = file.type || 'image/jpeg';

        var reader = new FileReader();
        reader.onload = function (ev) {
            imgEl.src = ev.target.result;
            modal.classList.add('open');
            document.body.classList.add('cropper-modal-active');

            if (cropper) {
                cropper.destroy();
            }
            cropper = new Cropper(imgEl, {
                aspectRatio: isNaN(aspectRatio) ? NaN : aspectRatio,
                viewMode: 1,
                autoCropArea: 1,
                responsive: true,
                background: false
            });
        };
        reader.readAsDataURL(file);
    });

    function closeModal(clearInput) {
        modal.classList.remove('open');
        document.body.classList.remove('cropper-modal-active');
        if (cropper) {
            cropper.destroy();
            cropper = null;
        }
        if (clearInput) {
            input.value = '';
        }
    }

    modal.querySelector('.cropper-close').addEventListener('click', function () {
        closeModal(true);
    });
    modal.querySelector('.cropper-cancel').addEventListener('click', function () {
        closeModal(true);
    });
    modal.addEventListener('click', function (e) {
        if (e.target === modal) closeModal(true);
    });

    modal.querySelector('.cropper-apply').addEventListener('click', function () {
        if (!cropper) return;

        cropper.getCroppedCanvas({ imageSmoothingQuality: 'high' }).toBlob(function (blob) {
            if (!blob) {
                closeModal(false);
                return;
            }

            var croppedFile = new File([blob], originalFileName, { type: originalFileType });

            // Replace the input's file list with the cropped version, so the
            // surrounding <form> uploads the cropped image instead of the original.
            var dataTransfer = new DataTransfer();
            dataTransfer.items.add(croppedFile);
            input.files = dataTransfer.files;

            if (preview) {
                var url = URL.createObjectURL(blob);
                if (preview.tagName === 'IMG') {
                    preview.src = url;
                } else {
                    preview.style.backgroundImage = 'url(' + url + ')';
                }
            }

            closeModal(false);
        }, originalFileType);
    });
}
