/*
 * (c) Kitodo. Key to digital objects e. V. <contact@kitodo.org>
 *
 * This file is part of the Kitodo project.
 *
 * It is licensed under GNU General Public License version 3 or later.
 *
 * For the full copyright and license information, please read the
 * LICENSE file that was distributed with this source code.
 */

$(document).ready(function(){

    // main menu toggle button for mobile view
    $('nav button.toggle').click(function(){
        $('nav.nav').toggleClass('show');
    });

    // initialize all dropdown menus
    $('.ui.dropdown').dropdown({
        on: 'hover'
    });

});