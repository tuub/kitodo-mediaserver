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

    // initialize all checkboxes
    $('.ui.checkbox').checkbox();

    // confirm user delete button
    $('a[href*="/users"][href*="/delete"]').click(function(e){
        e.preventDefault();
        $('#user-delete-modal')
            .modal({
                closable: false,
                onApprove: function(){
                    $(e.target).closest('form').submit();
                }
            })
            .modal('show');
    });

    // toggle action for password fields
    $('.action.input button.icon').click(function(e){
        e.preventDefault();
        button = $(e.target).closest('button');
        icon = button.find('i');
        input = button.siblings('input');

        if (icon.hasClass('slash')) {
            input.attr('type', 'password');
            icon.removeClass('slash')
        } else {
            input.attr('type', 'text');
            icon.addClass('slash')
        }
    });

});
