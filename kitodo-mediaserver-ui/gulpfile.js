var gulp = require('gulp');
var addsrc = require("gulp-add-src");
var del = require('del');

// copy jquery to static folder
gulp.task('build jquery', function () {
    return gulp.src([
        'node_modules/jquery/dist/jquery.min.js'
    ])
        .pipe(gulp.dest('src/main/webapp/static/jquery/'));
});

// copy semantic ui to static folder
gulp.task('build ui', function () {
    return gulp.src([
        'node_modules/semantic-ui-css/semantic.min.js',
        'node_modules/semantic-ui-css/semantic.min.css'
    ])
        .pipe(addsrc('node_modules/semantic-ui-css/themes/**', {base: 'node_modules/semantic-ui-css'}))
        .pipe(gulp.dest('src/main/webapp/static/semantic/'));
});

// build everything
gulp.task('build', [
    'build jquery',
    'build ui'
]);

// clean dist files
gulp.task('clean', function () {
    del([
        'src/main/webapp/static/jquery/',
        'src/main/webapp/static/semantic/'
    ]);
});
