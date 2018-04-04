var gulp = require('gulp');
var buildui = require('./semantic/tasks/build');

// copy jquery to static folder
gulp.task('build jquery', function () {
    return gulp.src(['node_modules/jquery/dist/**/*'])
        .pipe(gulp.dest('src/main/webapp/static/jquery/'));
});

// build semantic ui
gulp.task('build ui', buildui);

// build everything
gulp.task('build', [
    'build jquery',
    'build ui'
]);
