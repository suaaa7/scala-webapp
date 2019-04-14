import $ from 'jquery';
import 'materialize-css';
import Dropzone from 'dropzone';
import React from 'react';
import ReactDOM from 'react-dom';

// Dropzoneのドラッグ＆ドロップエリアエリアの設定
Dropzone.options.filedropzone = {
  paramName: 'file', // The name that will be used to transfer the file
  maxFilesize: 2, // MB
  dictDefaultMessage: '',
  thumbnailHeight: 200,
  thumbnailWidth: 200,
  init: function () {
    this.on('addedfile', function (file) {
      $('#overlaytext').val($('#overlaytext-shown').val());
      $('#overlaytextsize').val($('#overlaytextsize-shown').val());
    });
  },
  accept: function (file, done) {
    done();
    $('.dz-details').remove();
    $('.dz-progress').remove();
    $('.dz-error-message').remove();
    $('.dz-success-mark').remove();
    $('.dz-error-mark').remove();
  }
};

// materializedの設定
$(function () {
  $('.button-collapse').sideNav();
});

/**
 * Reactの画像表示エリア部品定義
 */
class Pictures extends React.component {

  constructor(props) {
    super(props);
    this.lastCreatedTime = null;
    this.state = { pictures: []};
  }

  componentDidMount() {
    this.updatePictures();
    this.timerID = setInterval(
      () => this.updatePictures(),
      5000
    );
  }

  componentWillUnmount() {
    clearInterval(this.timerID);
  }

  updatePictures() {
    var url = '/properties';

    fetch(this.appendLastCreatedDate(url)).then((res) => res.json()).then((json) => {
      const pictures = json.filter((p) => p.value.status === 'Success');
      if (pictures.length > 0) {
        this.lastCreatedTime = pictures[0].value.createdTime;
      }
      this.setState((prevState, props) => ({
        pictures: pictures.concat(prevState.pictures)
      }));
    });

    $('.dz-preview').hide('slow', function () { $(this).remove(); });
  }

  appendLastCreatedDate(url) {
    if (this.lastCreatedTime) {
      url = url + '?last_created_time=' + encodeURIComponent(this.lastCreatedTime);
    }
    return url;
  }

  render() {
    const pictureItems = this.state.pictures.map((picture) =>
      <div className="col s3" key={picture.id}>
        <div className="card">
          <div className="card-image">
            <a href={'/pictures' + picture.id}>
              <img src={'/pictures' + picture.id} height="150px" />
            </a>
          </div>
        </div>
      </div>
    );
    return (<div id="picture-grid" className="row center"> {pictureItems} </div>)
  }
}

// React Componentのレンダリング
ReactDOM.render(
  <Pictures />,
  document.getElementById('picture-grid')
);